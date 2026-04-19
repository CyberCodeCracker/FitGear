package com.amouri_coding.FitGear.user.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients/me")
@RequiredArgsConstructor
@Tag(name = "Client Subscription")
public class ClientSubscriptionController {

    private final ClientSubscriptionService service;

    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @GetMapping("/coach")
    public ResponseEntity<ClientCoachResponse> getMyCoach(Authentication authentication) {
        return ResponseEntity.ok(service.getMyCoach(authentication));
    }

    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @PostMapping("/coach/{coachId}")
    public ResponseEntity<ClientCoachResponse> subscribe(
            @PathVariable Long coachId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.subscribe(coachId, authentication));
    }

    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @DeleteMapping("/coach")
    public ResponseEntity<Void> unsubscribe(Authentication authentication) {
        service.unsubscribe(authentication);
        return ResponseEntity.noContent().build();
    }
}

