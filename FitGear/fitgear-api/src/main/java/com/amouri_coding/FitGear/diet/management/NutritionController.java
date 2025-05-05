package com.amouri_coding.FitGear.diet.management;

import com.amouri_coding.FitGear.diet.diet_day.DietDayRequest;
import com.amouri_coding.FitGear.diet.diet_program.DietProgramRequest;
import com.amouri_coding.FitGear.diet.diet_program.DietProgramResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients/{clientId}/nutrition")
@RequiredArgsConstructor
@Tag(name = "nutrition")
public class NutritionController {

    private final NutritionService service;

    @PostMapping("/diet")
    @PreAuthorize("hasRole('ROLE_COACH')")
    @ResponseStatus(HttpStatus.CREATED)
    public void assignDietProgram(
            @PathVariable final Long clientId,
            @RequestBody @Valid DietProgramRequest request,
            Authentication authentication
            ) {
        service.assignDietProgram(clientId, request, authentication);
    }

    @GetMapping("/{program-id}")
    @PreAuthorize("hasRole('ROLE_COACH')")
    public ResponseEntity<DietProgramResponse> getDietProgram(
            @PathVariable final Long clientId,
            @PathVariable(value = "program-id") Long programId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.getDietProgram(clientId, programId, authentication));
    }

    @DeleteMapping("/{program-id}/delete")
    @PreAuthorize("hasRole('ROLE_COACH')")
    public void deleteDietProgram(
            @PathVariable final Long clientId,
            @PathVariable Long programId,
            Authentication authentication
    ) {
        service.deleteDietProgram(clientId, programId, authentication);
    }

    @PostMapping("/{program-id}/add-day")
    @PreAuthorize("hasRole('ROLE_COACH')")
    @ResponseStatus(HttpStatus.CREATED)
    public void addDietDay(
            @PathVariable final Long clientId,
            @PathVariable(value = "program-id") Long programId,
            @RequestBody @Valid DietDayRequest request,
            Authentication authentication
    ) {
        service.addDietDay(clientId, programId, request, authentication);
    }
}
