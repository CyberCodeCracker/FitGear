package com.amouri_coding.FitGear.invite;

import com.amouri_coding.FitGear.auth.ClientRegistrationRequest;
import com.amouri_coding.FitGear.auth.InvitationRequest;
import com.amouri_coding.FitGear.security.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("invite")
@RequiredArgsConstructor
@Tag(name = "Invite")
@Slf4j
public class InvitationController {

    private final InvitationService invitationService;
    private final JwtService jwtService;

    @PreAuthorize("hasAuthority('ROLE_COACH')")
    @PostMapping("/invite")
    public ResponseEntity<?> inviteClient(
            @RequestBody @Valid InvitationRequest request,
            Authentication authentication
    ) {
        try {
            String invitationLink = invitationService.generateInvitationLink(request, authentication);
            return ResponseEntity.ok(Map.of("invitationLink", invitationLink));
        } catch (Exception e) {
            log.error("Error generating invitation link {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error","Generating invitation link: " + e.getMessage()));
        }

    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> registerClient(
            @RequestBody @Valid ClientRegistrationRequest request,
            @RequestParam String invitationLink
    ) {
        try {
            invitationService.registerClientFromInvitationLink(request, invitationLink);
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            log.error("Error registering client from link {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("Error","Registration failed: " + e.getMessage()));
        }
    }
}
