package com.amouri_coding.FitGear.auth;

import com.amouri_coding.FitGear.email.EmailService;
import com.amouri_coding.FitGear.email.EmailTemplateName;
import com.amouri_coding.FitGear.exception.InvalidTokenException;
import com.amouri_coding.FitGear.role.UserRole;
import com.amouri_coding.FitGear.role.UserRoleRepository;
import com.amouri_coding.FitGear.security.*;
import com.amouri_coding.FitGear.user.User;
import com.amouri_coding.FitGear.user.UserRepository;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.client.ClientRepository;
import com.amouri_coding.FitGear.user.coach.Coach;
import com.amouri_coding.FitGear.user.coach.CoachRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final CoachRepository coachRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final ClientRepository clientRepository;

    @Value("${spring.application.security.jwt.access-expiration}")
    private long accessTokenExpiration;

    @Value("${spring.application.security.jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    @Value("${spring.application.mail.frontend.activation-url}")
    private String activationUrl;

    @Value("${spring.application.security.url.base-url}")
    private String baseUrl;

    @Value("${spring.application.security.jwt.invitation-expiration}")
    private long invitationTokenExpiration;

    public String refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("Refresh token is missing");
        }

        String userEmail = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new SecurityException("Invalid refresh token");
        }

        return jwtService.generateRefreshToken(userDetails);
    }

    public void registerCoach(CoachRegistrationRequest request, HttpServletResponse response) throws MessagingException {
        Optional<Coach> coachExists = coachRepository.findByEmail(request.getEmail());

        if (coachExists.isPresent()) {
            throw new IllegalStateException("Coach with this email " + request.getEmail() + " already exists");
        }

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        UserRole coachRole = userRoleRepository.findByName("COACH")
                .orElseThrow(() -> new IllegalStateException("Coach Role not found"));

        Map<String, Object> claims = new HashMap<>();
        String fullName = request.getFirstName() + " " + request.getLastName();
        claims.put("role", coachRole.getName());
        claims.put("fullName", fullName);

        Coach coach = Coach.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountEnabled(false)
                .accountLocked(false)
                .createdAt(LocalDateTime.now())
                .roles(List.of(coachRole))
                .monthlyRate(request.getMonthlyRate())
                .description(request.getDescription())
                .phoneNumber(request.getPhoneNumber())
                .yearsOfExperience(request.getYearsOfExperience())
                .build()
                ;

        coachRepository.save(coach);

        String accessToken = jwtService.generateAccessToken(claims, coach);
        String refreshToken = jwtService.generateRefreshToken(coach);

        saveToken(coach, refreshToken, TokenType.REFRESH);

        response.setHeader("Authorization", "Bearer " + accessToken);
        sendValidationEmail(coach);
    }

    public void registerClient(ClientRegistrationRequest request, HttpServletResponse response) throws MessagingException {
        Optional<Client> clientExists = clientRepository.findByEmail(request.getEmail());

        if (clientExists.isPresent()) {
            throw new IllegalStateException("Client with this email " + request.getEmail() + " already exists");
        }

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        UserRole userRole = userRoleRepository.findByName("CLIENT")
                .orElseThrow(() -> new IllegalStateException("Client Role not found"));

        Map<String, Object> claims = new HashMap<>();
        String fullName = request.getFirstName() + " " + request.getLastName();
        claims.put("role", userRole.getName());
        claims.put("fullName", fullName);

        Client client = Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountEnabled(false)
                .accountLocked(false)
                .createdAt(LocalDateTime.now())
                .roles(List.of(userRole))
                .height(request.getHeight())
                .weight(request.getWeight())
                .bodyFatPercentage(request.getBodyFatPercentage())
                .build()
                ;

        clientRepository.save(client);

        String accessToken = jwtService.generateAccessToken(claims, client);
        String refreshToken = jwtService.generateRefreshToken(client);

        saveToken(client, refreshToken, TokenType.REFRESH);

        response.setHeader("Authorization", "Bearer " + accessToken);
        sendValidationEmail(client);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        String newToken = generateAndSaveActivationToken(user);

        try {
            emailService.sendEmail(
                    user.getEmail(),
                    user.getName(),
                    EmailTemplateName.ACTIVATE_ACCOUNT,
                    activationUrl,
                    newToken,
                    "Account activation");
        } catch (MessagingException e) {
            System.out.println("Exception " + e.getMessage());
        }
    }

    private void saveToken(User user, String token, TokenType tokenType) {

        UserType userType;
        Class<?> userClass = Hibernate.getClass(user);
        if (Coach.class.isAssignableFrom(userClass)) {
            userType = UserType.COACH;
        } else if (Client.class.isAssignableFrom(userClass)) {
            userType = UserType.CLIENT;
        } else {
            throw new IllegalArgumentException("Invalid user type");
        }
        if (user.getId() == null) {
            userRepository.save(user);
        }

        long expiresAt;
        if (tokenType == TokenType.REFRESH) {
            expiresAt = refreshTokenExpiration;
        } else {
            return;
        }
        Token tokenEntity = Token.builder()
                .token(token)
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plus(expiresAt, ChronoUnit.MILLIS))
                .tokenType(tokenType)
                .user(user)
                .userType(userType)
                .build()
                ;

        tokenRepository.save(tokenEntity);
    }

    private String generateAndSaveActivationToken(User user) {
        var generatedToken = generateActivationTokenBody(6);

        UserType userType;
        Class<?> userClass = Hibernate.getClass(user);
        if (Coach.class.isAssignableFrom(userClass)) {
            userType = UserType.COACH;
        } else if (Client.class.isAssignableFrom(userClass)) {
            userType = UserType.CLIENT;
        } else {
            throw new IllegalArgumentException("Invalid user type.");
        }
        var token = Token.builder()
                .token(generatedToken)
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .tokenType(TokenType.BEARER)
                .userType(userType)
                .user(user)
                .build()
                ;
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationTokenBody(int length) {
        String charSequence = "0123456789";
        StringBuilder activationToken = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(charSequence.length());
            activationToken.append(charSequence.charAt(randomIndex));
        }
        return activationToken.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Map<String, Object> claims = new HashMap<>();
        var user = (User) auth.getPrincipal();
        if (user.getRoles().stream().anyMatch(userRole -> "COACH".equals(userRole.getName()))) {
            claims.put("role", "ROLE_COACH");
        } else if (user.getRoles().stream().anyMatch(userRole -> "CLIENT".equals(userRole.getName()))) {
            claims.put("role", "ROLE_CLIENT");
        } else {
            throw new IllegalStateException("User role not recognized");
        }
        claims.put("fullName", user.getName());

        String accessToken = jwtService.generateAccessToken(claims, (User) auth.getPrincipal());
        String refreshToken = jwtService.generateRefreshToken((User) auth.getPrincipal());
        return AuthenticationResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .build()
                ;
    }

    @Transactional
    public void confirmAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token expired. A new token has been issued.");
        }
        try {
            User user = userRepository.findById(savedToken.getUser().getId())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            if (savedToken.getToken().equals(token)) {
                user.setAccountEnabled(true);
                userRepository.save(user);
                savedToken.setValidatedAt(LocalDateTime.now());
                tokenRepository.save(savedToken);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    public String generateInvitationLink(InvitationRequest request, Authentication authentication) {

        if (authentication == null) {
            log.error("No authentication found.");
            throw new AccessDeniedException("Authentication required");
        }

        if (!authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COACH"))) {
            throw new AccessDeniedException("You are not a coach. Illegal operation.");
        }

        Object principal = authentication.getPrincipal();
        Coach coach = ((Coach) principal);

        Map<String, Object> claims = new HashMap<>();
        claims.put("coachId", coach.getId());
        claims.put("coachName", coach.getName());
        claims.put("coachEmail", coach.getEmail());
        claims.put("role", coach.getName());
        if (!request.getMessage().isEmpty() && !request.getMessage().isBlank()) {
            claims.put("message", request.getMessage());
        }
        claims.put("expirationTime", invitationTokenExpiration);
        log.info("Extracted claims: {}", claims);
        String jwtToken = jwtService.generateInvitationToken(claims, coach);
        String invitationLink = baseUrl + "/auth/invite?token=" + jwtToken;

        return invitationLink;
    }

    public void registerClientFromInvitationLink(ClientRegistrationRequest request,
                                                 String invitationLink,
                                                 HttpServletResponse response
    ) throws MessagingException {

        Optional<Client> clientExists = clientRepository.findByEmail(request.getEmail());

        if (clientExists.isPresent()) {
            throw new IllegalStateException("Client with this email " + request.getEmail() + " already exists.");
        }

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("Password and confirm password do not match.");
        }

        UserRole userRole = userRoleRepository.findByName("CLIENT")
                .orElseThrow(() -> new IllegalStateException("Client Role not found"));

        String token = extractTokenFromInvitationLink(invitationLink);
        Integer coachId = jwtService.extractClaim(token, claims -> claims.get("coachId", Integer.class));
        Coach coach = coachRepository.findById(coachId);
        if (coach == null) {
            throw new IllegalStateException("Coach not found.");
        }
        log.info("Coach Id {}", coachId);

        Map<String, Object> claims = new HashMap<>();
        claims.put("coachId", coachId);
        claims.put("role", userRole.getName());
        claims.put("fullName", request.getFirstName() + " " + request.getLastName());

        Client client = Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .coach(coach)
                .password(passwordEncoder.encode(request.getPassword()))
                .accountEnabled(false)
                .accountLocked(false)
                .createdAt(LocalDateTime.now())
                .roles(List.of(userRole))
                .height(request.getHeight())
                .weight(request.getWeight())
                .bodyFatPercentage(request.getBodyFatPercentage())
                .build()
                ;
        clientRepository.save(client);

        String accessToken = jwtService.generateAccessToken(claims, client);
        String refreshToken = jwtService.generateRefreshToken(client);

        saveToken(client, refreshToken, TokenType.REFRESH);

        response.setHeader("Authorization", "Bearer " + accessToken);
        sendValidationEmail(client);    }

    private String extractTokenFromInvitationLink(String invitationLink) {

        int tokenStartingIndex = invitationLink.indexOf("token=");
        if (tokenStartingIndex == -1) {
            throw new IllegalArgumentException("Invalid invitation link.");
        }
        return invitationLink.substring(tokenStartingIndex + "token=".length());
    }

}
