package com.amouri_coding.FitGear.training.training_program;

import com.amouri_coding.FitGear.training.training_day.TrainingDayRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class TrainingProgramRequest {

    private List<TrainingDayRequest> trainingday;

    @NotBlank
    private String title;

    @NotBlank
    private String description;
}
