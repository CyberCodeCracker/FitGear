package com.amouri_coding.FitGear.diet.management;

import com.amouri_coding.FitGear.diet.diet_program.DietProgramRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public void assignDietingProgram(
            @PathVariable final Long clientId,
            @RequestBody @Valid DietProgramRequest request,
            Authentication authentication
            ) {
        service.assignDietingProgram(clientId, request, authentication);
    }
}
