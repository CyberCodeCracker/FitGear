package com.amouri_coding.FitGear.training.training_program;

import com.amouri_coding.FitGear.training.training_day.TrainingDayRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class TrainingProgramRequest {

    @NotEmpty(message = "Days can't be empty")
    private List<TrainingDayRequest> trainingDays;
}
