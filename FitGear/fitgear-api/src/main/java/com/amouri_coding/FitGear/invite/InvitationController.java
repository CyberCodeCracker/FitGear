package com.amouri_coding.FitGear.invite;

import com.amouri_coding.FitGear.auth.InvitationRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("invite")
@RequiredArgsConstructor
@Tag(name = "Invite")
public class InvitationController {

    private final InvitationService invitationService;

    @PreAuthorize("hasAuthority('ROLE_COACH')")
    @PostMapping("/invite")
    public ResponseEntity<?> inviteClient(
            @RequestBody @Valid InvitationRequest request,
            Authentication authentication
    ) {
        String invitationLink = invitationService.generateInvitationLink(request, authentication);
        return ResponseEntity.ok(Map.of("invitationLink", invitationLink));
    }
}
