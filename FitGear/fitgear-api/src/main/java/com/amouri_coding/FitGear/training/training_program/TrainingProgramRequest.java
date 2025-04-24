package com.amouri_coding.FitGear.training.training_program;

import com.amouri_coding.FitGear.training.training_day.TrainingDayRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Builder
public class TrainingProgramRequest {

    @NotBlank(message = "Day Can't be blank")
    private List<TrainingDayRequest> trainingday;
}
