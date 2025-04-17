package com.amouri_coding.FitGear.auth;

import com.amouri_coding.FitGear.user.coach.Coach;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {

    private final AuthenticationService authService;
    private final ValidationAutoConfiguration validationAutoConfiguration;

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> generateRefreshToken(
            @RequestBody RefreshTokenRequest request
    ) {
        try {
            String refreshToken = request.getRefreshToken();
            authService.refreshToken(refreshToken);
            return ResponseEntity.ok(Map.of("token", refreshToken));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register/coach")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> registerCoach(
            @RequestBody @Valid CoachRegistrationRequest request,
            HttpServletResponse response
    ) throws MessagingException {
        authService.registerCoach(request, response);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/register/client")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> registerClient(
            @RequestBody @Valid ClientRegistrationRequest request,
            HttpServletResponse response
    ) throws MessagingException {
        authService.registerClient(request, response);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/confirm-account")
    public void confirmAccount(
            @RequestParam String token
    ) throws MessagingException {
        authService.confirmAccount(token);
    }

}

