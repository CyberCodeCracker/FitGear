package com.amouri_coding.FitGear.invite;

import com.amouri_coding.FitGear.auth.ClientRegistrationRequest;
import com.amouri_coding.FitGear.auth.InvitationRequest;
import com.amouri_coding.FitGear.role.UserRole;
import com.amouri_coding.FitGear.role.UserRoleRepository;
import com.amouri_coding.FitGear.security.JwtService;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.client.ClientRepository;
import com.amouri_coding.FitGear.user.coach.Coach;
import com.amouri_coding.FitGear.user.coach.CoachRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationService {

    private final CoachRepository coachRepository;
    @Value("${spring.application.security.url.base-url}")
    private String baseUrl;

    @Value("${spring.application.security.jwt.invitation-expiration}")
    private long invitationTokenExpiration;

    private final JwtService jwtService;
    private final ClientRepository clientRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public String generateInvitationLink(InvitationRequest request, Authentication authentication) {

        if (authentication == null) {
            log.warn("No authentication found.");
            throw new AccessDeniedException("Authentication required");
        }

        Object principal = authentication.getPrincipal();
        if (!authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COACH"))) {
            throw new AccessDeniedException("You are not a coach. Illegal operation.");
        }

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

    public void registerClientFromInvitationLink(ClientRegistrationRequest request, String invitationLink) {

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
        Integer coachId = jwtService.extractClaim(token, claims -> claims.get("CoachId", Integer.class));
        Coach coach = coachRepository.findById(coachId);

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
    }

    private String extractTokenFromInvitationLink(String invitationLink) {

        int tokenStartingIndex = invitationLink.indexOf("token=");
        if (tokenStartingIndex == -1) {
            throw new IllegalArgumentException("Invalid invitation link.");
        }
        return invitationLink.substring(tokenStartingIndex + "token=".length());
    }
}
