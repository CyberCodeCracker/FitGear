package com.amouri_coding.FitGear.training.exercise;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExerciseRequest {

    private Long id;

    @NotBlank(message = "Title can't be blank")
    private String title;
    private String exerciseUrl;

    @NotNull(message = "Rest time can't be null")
    @Min(value = 0, message = "Rest time can't be negative")
    private int restTime;

    @NotNull(message = "Exercise number can't be null")
    @Min(value = 0, message = "Exercise number can't be negative")
    private int exerciseNumber;

    @NotNull(message = "Number of sets can't be null")
    @Min(value = 1, message = "Number of sets must be at least 1")
    private int numberOfSets;

    @NotNull(message = "Number of reps can't be null")
    @Min(value = 1, message = "Number of reps must be at least 1")
    private int numberOfReps;
}
