package com.amouri_coding.FitGear.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
@AllArgsConstructor
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws IOException, ServletException {

        log.info("JwtFilter: Processing request");
        log.info("Request URI: {}", request.getRequestURI());
        log.info("Servlet Path: {}", request.getServletPath());
        log.info("Context Path: {}", request.getContextPath());
        log.info("Request Method: {}", request.getMethod());

        try {
            if (request.getRequestURI().contains("/api/v1/auth")) {
                System.out.println("Passing through auth endpoint");
                filterChain.doFilter(request, response);
                System.out.println("Finished passing through auth endpoint");
                return;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(token);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException e) {
            log.warn("JWT is expired: {}", e.getMessage());
        } catch (JwtException | UsernameNotFoundException e) {
            log.warn("JWT authentication failed: {}", e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

}
