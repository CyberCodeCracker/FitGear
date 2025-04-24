package com.amouri_coding.FitGear.training.exercise;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public class ExerciseRequest {

    @NotNull(message = "Training day id can't be null")
    private Long trainingDayId;

    @NotBlank(message = "Title can't be blank")
    private String title;

    @NotBlank(message = "Rest Time can't be blank")
    private String restTime;

    private String exerciseUrl;

    @NotNull(message = "Number of set can't be null")
    private int numberOfSets;

    @NotNull(message = "Number of reps can't be null")
    private int numberOfReps;
}
