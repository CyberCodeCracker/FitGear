package com.amouri_coding.FitGear.training.performance;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients/me/exercises/{exerciseId}/performances")
@RequiredArgsConstructor
@Tag(name = "Exercise Performance")
public class ExercisePerformanceController {

    private final ExercisePerformanceService service;

    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @GetMapping
    public ResponseEntity<List<ExercisePerformanceResponse>> list(
            @PathVariable Long exerciseId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.list(exerciseId, authentication));
    }

    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ExercisePerformanceResponse> add(
            @PathVariable Long exerciseId,
            @RequestBody @Valid ExercisePerformanceRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(exerciseId, request, authentication));
    }
}

