package com.amouri_coding.FitGear.diet.diet_program;

import com.amouri_coding.FitGear.diet.diet_day.DietDayRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class DietProgramRequest {

    @NotEmpty(message = "Title can't be null")
    private String title;

    @NotEmpty(message = "Description can't be null")
    private String description;

    @NotEmpty(message = "Days can't be null")
    private List<DietDayRequest> days;
}
