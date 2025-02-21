package com.amouri_coding.FitGear.auth;

import com.amouri_coding.FitGear.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public String refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("Refresh token is missing");
        }

        String userEmail = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        if (!jwtService.isRefreshTokenValid(refreshToken, userDetails)) {
            throw new SecurityException("Invalid refresh token");
        }

        return jwtService.generateRefreshToken(userDetails);
    }
}
