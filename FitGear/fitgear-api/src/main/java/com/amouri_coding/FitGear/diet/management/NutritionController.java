package com.amouri_coding.FitGear.diet.management;

import com.amouri_coding.FitGear.diet.diet_day.DietDayRequest;
import com.amouri_coding.FitGear.diet.diet_day.DietDayResponse;
import com.amouri_coding.FitGear.diet.diet_program.DietProgramRequest;
import com.amouri_coding.FitGear.diet.diet_program.DietProgramResponse;
import com.amouri_coding.FitGear.diet.meal.MealRequest;
import com.amouri_coding.FitGear.diet.meal.MealResponse;
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

    @DeleteMapping("/{program-id}/delete-program")
    @PreAuthorize("hasRole('ROLE_COACH')")
    public void deleteDietProgram(
            @PathVariable final Long clientId,
            @PathVariable(value = "program-id") Long programId,
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

    @GetMapping("/{program-id}/days/{day-id}")
    @PreAuthorize("hasRole('ROLE_COACH')")
    public ResponseEntity<DietDayResponse> getDietDay(
            @PathVariable final Long clientId,
            @PathVariable(value = "program-id") Long programId,
            @PathVariable(value = "day-id") Long dayId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.getDietDay(clientId, programId, dayId, authentication));
    }

    @PatchMapping("/{program-id}/days/{day-id}/edit-day")
    @PreAuthorize("hasRole('ROLE_COACH')")
    public void editDietDay(
            @PathVariable final Long clientId,
            @PathVariable(value = "program-id") Long programId,
            @PathVariable(value = "day-id") Long dayId,
            @RequestBody @Valid DietDayRequest request,
            Authentication authentication
    ) {
        service.editDietDay(clientId, programId, dayId, request, authentication);
    }

    @DeleteMapping("/{program-id}/days/{day-id}/delete-day")
    @PreAuthorize("hasRole('ROLE_COACH')")
    public void deleteDietDay(
            @PathVariable final Long clientId,
            @PathVariable(value = "program-id") Long programId,
            @PathVariable(value = "day-id") Long dayId,
            Authentication authentication
    ) {
        service.deleteDietDay(clientId, programId, dayId, authentication);
    }

    @PostMapping("/{program-id}/days/{day-id}/add-meal")
    @PreAuthorize("hasRole('ROLE_COACH')")
    @ResponseStatus(HttpStatus.CREATED)
    public void addMeal(
            @PathVariable final Long clientId,
            @PathVariable(value = "program-id") Long programId,
            @PathVariable(value = "day-id") Long dayId,
            @RequestBody @Valid MealRequest request,
            Authentication authentication
    ) {
        service.addMeal(clientId, programId, dayId, request, authentication);
    }

    @GetMapping("{program-id}/days/{day-id}/meals/{meal-id}")
    @PreAuthorize("hasRole('ROLE_COACH')")
    public ResponseEntity<MealResponse> getMeal(
            @PathVariable final Long clientId,
            @PathVariable(value = "program-id") Long programId,
            @PathVariable(value = "day-id") Long dayId,
            @PathVariable(value = "meal-id") Long mealId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.getMeal(clientId, programId, dayId, mealId, authentication));
    }

    @PatchMapping("{program-id}/days/{day-id}/meals/{meal-id}/edit")
    @PreAuthorize("hasRole('ROLE_COACH')")
    public void editMeal(
            @PathVariable final Long clientId,
            @PathVariable(value = "program-id") Long programId,
            @PathVariable(value = "day-id") Long dayId,
            @PathVariable(value = "meal-id") Long mealId,
            @RequestBody MealRequest request,
            Authentication authentication
    ) {
        service.editMeal(clientId, programId, dayId, mealId, request, authentication);
    }

    @DeleteMapping("{program-id}/days/{day-id}/meals/{meal-id}/delete")
    @PreAuthorize("hasRole('ROLE_COACH')")
    public void deleteMeal(
            @PathVariable final Long clientId,
            @PathVariable(value = "program-id") Long programId,
            @PathVariable(value = "day-id") Long dayId,
            @PathVariable(value = "meal-id") Long mealId,
            Authentication authentication
    ) {
        service.deleteMeal(clientId, programId, dayId, mealId, authentication)
    }
}
