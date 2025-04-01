package com.amouri_coding.FitGear.auth;

import com.amouri_coding.FitGear.coach.Coach;
import com.amouri_coding.FitGear.coach.CoachRepository;
import com.amouri_coding.FitGear.email.EmailService;
import com.amouri_coding.FitGear.email.EmailTemplateName;
import com.amouri_coding.FitGear.role.UserRoleRepository;
import com.amouri_coding.FitGear.security.*;
import com.amouri_coding.FitGear.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

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

    @Value("${spring.application.security.jwt.access-expiration}")
    private long accessTokenExpiration;

    @Value("${spring.application.security.jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    @Value("${spring.application.mail.frontend.activation-url}")
    private String activationUrl;
    private UserRepository userRepository;

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
            throw new IllegalArgumentException("Coach passwords do not match");
        }

        var coachRole = userRoleRepository.findByName("COACH")
                .orElseThrow(() -> new IllegalStateException("Coach Role not found"));

        var coach = Coach.builder()
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
                .build();

        coachRepository.save(coach);

        String accessToken = jwtService.generateAccessToken(coach);
        String refreshToken = jwtService.generateRefreshToken(coach);

        saveToken(coach, accessToken, TokenType.ACCESS);
        saveToken(coach, refreshToken, TokenType.REFRESH);

        response.setHeader("Authorization", "Bearer " + accessToken);
        sendValidationEmail(coach);
    }

    private void sendValidationEmail(Coach coach) throws MessagingException {
        var newToken = generateAndSaveActivationToken(coach);

        try {
            emailService.sendEmail(
                    coach.getEmail(),
                    coach.getName(),
                    EmailTemplateName.ACTIVATE_ACCOUNT,
                    activationUrl,
                    newToken,
                    "Account activation");
        } catch (MessagingException e) {
            System.out.println("Exception " + e.getMessage());
        }
    }

    private void saveToken(Coach coach, String token, TokenType tokenType) {

        if (coach.getId() == null) {
            coach = userRepository.save(coach);
        }

        Long expiresAt;
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
                .user(coach)
                .userType(UserType.COACH)
                .build()
                ;

        tokenRepository.save(tokenEntity);
    }

    private String generateAndSaveActivationToken(Coach coach) {
        var generatedToken = generateActivationToken(6);
        var token = Token.builder()
                .token(generatedToken)
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .tokenType(TokenType.BEARER)
                .user(coach)
                .build()
                ;
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationToken(int length) {
        String charSequence = "0123456789";
        StringBuilder activationToken = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(charSequence.length());
            activationToken.append(charSequence.charAt(randomIndex));
        }
        return activationToken.toString();
    }

}
