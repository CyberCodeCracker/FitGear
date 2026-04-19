package com.amouri_coding.FitGear.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler({AccessDeniedException.class, AuthenticationException.class})
    public ResponseEntity<String> accessDeniedExceptionHandler(AccessDeniedException ex) {
        System.out.println("Access denied: " + ex.getMessage());
        return ResponseEntity.status((HttpStatus.FORBIDDEN)).body("Access denied: " + ex.getMessage());
    }
}
