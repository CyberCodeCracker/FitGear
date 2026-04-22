package com.amouri_coding.FitGear.training.training_day;

import com.amouri_coding.FitGear.common.DayOfWeek;
import com.amouri_coding.FitGear.training.exercise.ExerciseRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class TrainingDayRequest {

    private Long programId;

    @NotBlank(message = "Title can't be blank")
    private String title;

    @NotEmpty(message = "Exercises can't be empty")
    private List<ExerciseRequest> exercises;

    @NotNull(message = "Estimated burned calories can't be null")
    @Min(value = 0, message = "Estimated burned calories can't be negative")
    private int estimatedBurnedCalories;

    @NotNull(message = "Day can't be null")
    private DayOfWeek day;
}
