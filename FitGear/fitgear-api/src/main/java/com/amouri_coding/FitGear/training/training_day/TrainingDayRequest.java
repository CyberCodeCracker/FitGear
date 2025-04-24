package com.amouri_coding.FitGear.training.training_day;

import com.amouri_coding.FitGear.common.DayOfWeek;
import com.amouri_coding.FitGear.training.exercise.ExerciseRequest;
import com.amouri_coding.FitGear.training.training_program.TrainingProgram;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class TrainingDayRequest {

    @NotNull(message = "Program can't be null")
    private Long programId;

    @NotBlank(message = "Title can't be blank")
    private String title;

    @NotEmpty(message = "Exercises can't be blank")
    private List<ExerciseRequest> exercises;

    @NotNull(message = "Estimated burned calories can't be null")
    private int estimatedBurnedCalories;

    @NotEmpty
    private DayOfWeek day;
}
