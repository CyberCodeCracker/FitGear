package com.amouri_coding.FitGear.invite;

import com.amouri_coding.FitGear.auth.InvitationRequest;
import com.amouri_coding.FitGear.security.JwtService;
import com.amouri_coding.FitGear.user.coach.Coach;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationService {

    @Value("${spring.application.security.url.base-url}")
    private String baseUrl;

    @Value("${spring.application.security.jwt.invitation-expiration}")
    private long invitationTokenExpiration;

    private final JwtService jwtService;

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
}
