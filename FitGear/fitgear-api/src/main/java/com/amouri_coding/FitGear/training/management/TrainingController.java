package com.amouri_coding.FitGear.training.management;

import com.amouri_coding.FitGear.training.exercise.ExerciseRequest;
import com.amouri_coding.FitGear.training.exercise.ExerciseResponse;
import com.amouri_coding.FitGear.training.training_day.TrainingDayRequest;
import com.amouri_coding.FitGear.training.training_day.TrainingDayResponse;
import com.amouri_coding.FitGear.training.training_program.TrainingProgramRequest;
import com.amouri_coding.FitGear.training.training_program.TrainingProgramResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients/{clientId}/training")
@RequiredArgsConstructor
@Tag(name = "Training")
public class TrainingController {

    private final TrainingService service;

    @PreAuthorize("hasRole('ROLE_COACH')")
    @PostMapping("/program")
    @ResponseStatus(HttpStatus.CREATED)
    public void assignTrainingProgram(
            @PathVariable Long clientId,
            @RequestBody @Valid TrainingProgramRequest request,
            Authentication authentication
    ) {
        service.assignProgram(clientId, request, authentication);
    }

    @PreAuthorize("hasRole('ROLE_COACH')")
    @GetMapping("/{programId}")
    public ResponseEntity<TrainingProgramResponse> getProgramOfClient(
            @PathVariable Long clientId,
            @PathVariable Long programId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.getProgramOfClient(clientId, programId, authentication));
    }

    @PreAuthorize("hasRole('ROLE_COACH')")
    @DeleteMapping("/{programId}")
    public void deleteTrainingProgram(
            @PathVariable Long clientId,
            @PathVariable Long programId,
            Authentication authentication
    ) {
        service.deleteTrainingProgram(programId, clientId, authentication);
    }

    @PreAuthorize("hasRole('ROLE_COACH')")
    @PostMapping("/{programId}/days")
    @ResponseStatus(HttpStatus.CREATED)
    public void addTrainingDay(
            @PathVariable Long clientId,
            @PathVariable Long programId,
            @RequestBody @Valid TrainingDayRequest request,
            Authentication authentication
    ) {
        service.addTrainingDay(programId, clientId, request, authentication);
    }

    @PreAuthorize("hasRole('ROLE_COACH')")
    @GetMapping("/{programId}/days/{dayId}")
    public ResponseEntity<TrainingDayResponse> getDayOfClient(
            @PathVariable Long clientId,
            @PathVariable Long programId,
            @PathVariable Long dayId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.getDayOfClient(dayId, clientId, programId, authentication));
    }

    @PreAuthorize("hasRole('ROLE_COACH')")
    @PatchMapping("/{programId}/days/{dayId}")
    public ResponseEntity<TrainingDayResponse> editTrainingDay(
            @PathVariable Long clientId,
            @PathVariable Long programId,
            @PathVariable Long dayId,
            @RequestBody @Valid TrainingDayRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.editTrainingDay(dayId, clientId, programId, request, authentication));
    }

    @PreAuthorize("hasRole('ROLE_COACH')")
    @DeleteMapping("/{programId}/days/{dayId}")
    public void deleteTrainingDay(
            @PathVariable Long clientId,
            @PathVariable Long programId,
            @PathVariable Long dayId,
            Authentication authentication
    ) {
        service.deleteTrainingDay(dayId, clientId, programId, authentication);
    }

    @PreAuthorize("hasRole('ROLE_COACH')")
    @PostMapping("/{programId}/days/{dayId}/exercises")
    @ResponseStatus(HttpStatus.CREATED)
    public void addExercise(
            @PathVariable Long clientId,
            @PathVariable Long programId,
            @PathVariable Long dayId,
            @RequestBody @Valid ExerciseRequest request,
            Authentication authentication
    ) {
        service.addExercise(programId, dayId, clientId, request, authentication);
    }

    @PreAuthorize("hasRole('ROLE_COACH')")
    @GetMapping("/{programId}/days/{dayId}/exercises/{exerciseId}")
    public ResponseEntity<ExerciseResponse> getExerciseOfClient(
            @PathVariable Long clientId,
            @PathVariable Long programId,
            @PathVariable Long dayId,
            @PathVariable Long exerciseId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.getExerciseOfClient(exerciseId, dayId, clientId, authentication));
    }

    @PreAuthorize("hasRole('ROLE_COACH')")
    @PatchMapping("/{programId}/days/{dayId}/exercises/{exerciseId}")
    public ResponseEntity<ExerciseResponse> editExercise(
            @PathVariable Long clientId,
            @PathVariable Long programId,
            @PathVariable Long dayId,
            @PathVariable Long exerciseId,
            @RequestBody @Valid ExerciseRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.editExercise(exerciseId, clientId, dayId, request, authentication));
    }

    @PreAuthorize("hasRole('ROLE_COACH')")
    @DeleteMapping("/{programId}/days/{dayId}/exercises/{exerciseId}")
    public void deleteExercise(
            @PathVariable Long clientId,
            @PathVariable Long programId,
            @PathVariable Long dayId,
            @PathVariable Long exerciseId,
            Authentication authentication
    ) {
        service.deleteExercise(exerciseId, clientId, programId, dayId, authentication);
    }
}