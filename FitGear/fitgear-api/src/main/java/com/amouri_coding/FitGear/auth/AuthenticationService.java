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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        Optional<Coach> coachExists = coachRepository.findByEmail(request.getCoachEmail());

        if (coachExists.isPresent()) {
            throw new IllegalStateException("Coach already exists");
        }

        if (!request.getCoachPassword().equals(request.getCoachPasswordConfirm())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        UserRole coachRole = userRoleRepository.findByName("COACH")
                .orElseThrow(() -> new IllegalStateException("Coach Role not found"));

        Map<String, Object> claims = new HashMap<>();
        String fullName = request.getCoachFirstName() + " " + request.getCoachLastName();
        claims.put("role", coachRole.getName());
        claims.put("fullName", fullName);

        Coach coach = Coach.builder()
                .firstName(request.getCoachFirstName())
                .lastName(request.getCoachLastName())
                .email(request.getCoachEmail())
                .password(passwordEncoder.encode(request.getCoachPassword()))
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

        saveToken(coach, accessToken, TokenType.ACCESS);
        saveToken(coach, refreshToken, TokenType.REFRESH);

        response.setHeader("Authorization", "Bearer " + accessToken);
        sendValidationEmail(coach);
    }

    public void registerClient(ClientRegistrationRequest request, HttpServletResponse response) throws MessagingException {
        Optional<Client> clientExists = clientRepository.findByEmail(request.getEmail());

        if (clientExists.isPresent()) {
            throw new IllegalStateException("Client already exists");
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

        saveToken(client, accessToken, TokenType.ACCESS);
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
            log.info("unexpected user type " + user.getClass().getSimpleName());
            throw new IllegalArgumentException("Invalid user type");
        }
        if (user.getId() == null) {
            userRepository.save(user);
        }

        long expiresAt;
        if (tokenType == TokenType.ACCESS) {
            expiresAt = accessTokenExpiration;
        } else {
            expiresAt = refreshTokenExpiration;
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
            log.info("unexpected user type " + user.getClass().getSimpleName());
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
            claims.put("role", "COACH");
        } else if (user.getRoles().stream().anyMatch(userRole -> "CLIENT".equals(userRole.getName()))) {
            claims.put("role", "CLIENT");
        } else {
            throw new IllegalStateException("User role not recognized");
        }
        claims.put("fullName", user.getName());

        String accessToken = jwtService.generateAccessToken(claims, user);
        String refreshToken = jwtService.generateRefreshToken(user);
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


}
