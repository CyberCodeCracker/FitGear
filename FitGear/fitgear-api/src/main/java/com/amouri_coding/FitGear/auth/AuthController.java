package com.amouri_coding.FitGear.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {

    private final AuthenticationService authService;

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(
            @RequestBody Map<String, String> request
    ) {
        try {
            String refreshToken = request.get("refresh_token");
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
    public ResponseEntity<?> register(
            @RequestBody @Valid CoachRegistrationRequest request,
            HttpServletResponse response
    ) throws MessagingException {
        authService.registerCoach(request, response);
        return ResponseEntity.accepted().build();
    }
}

