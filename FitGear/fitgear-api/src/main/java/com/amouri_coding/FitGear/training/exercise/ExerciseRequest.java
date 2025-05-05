package com.amouri_coding.FitGear.training.exercise;

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

    @NotNull(message = "Rest Time can't be blank")
    private int restTime;
    @NotNull(message = "Exercise number can't be null")
    private int exerciseNumber;
    @NotNull(message = "Number of set can't be null")
    private int numberOfSets;
    @NotNull(message = "Number of reps can't be null")
    private int numberOfReps;
}
