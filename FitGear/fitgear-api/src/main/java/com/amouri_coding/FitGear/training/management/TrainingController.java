package com.amouri_coding.FitGear.training.management;

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
@RequiredArgsConstructor
@RequestMapping("training")
@Tag(name = "Training")
public class TrainingController {

    private final TrainingService service;

    @PreAuthorize(value = "hasRole('ROLE_COACH')")
    @PostMapping("/assign-program")
    @ResponseStatus(HttpStatus.CREATED)
    public void assignProgram(
            @RequestParam Long clientId,
            @RequestBody @Valid TrainingProgramRequest request,
            Authentication authentication
    ) {
        service.assignProgram(clientId, request, authentication);
    }

    @PreAuthorize(value = "hasRole('ROLE_COACH')")
    @GetMapping("/get-program")
    public ResponseEntity<TrainingProgramResponse> getProgramOfClient(
            @RequestParam Long clientId,
            @RequestParam Long programId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.getProgramOfClient(clientId, programId, authentication));
    }

    @PreAuthorize(value = "hasRole('ROLE_COACH')")
    @PatchMapping("/edit-day/{day-id}")
    public ResponseEntity<TrainingDayResponse> editDay(
            @PathVariable(value = "day-id") Long dayId,
            @RequestParam Long clientId,
            @RequestParam Long coachId,
            @RequestParam Long programId,
            @RequestBody TrainingDayRequest request,
            Authentication authentication
            ) {
        TrainingDayResponse updatedTrainingDay = service.editTrainingDay(dayId, clientId, coachId, programId, request, authentication);
        return ResponseEntity.ok(updatedTrainingDay);
    }

}
