package com.amouri_coding.FitGear.training.performance;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExercisePerformanceRequest {

    // Optional; defaults to today
    private LocalDate performedAt;

    @NotNull(message = "Number of sets is required")
    @Min(value = 1, message = "Number of sets must be positive")
    private Integer numberOfSets;

    @NotNull(message = "Number of reps is required")
    @Min(value = 1, message = "Number of reps must be positive")
    private Integer numberOfReps;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.0", message = "Weight must be positive")
    private Double weight;

    private String notes;
}
