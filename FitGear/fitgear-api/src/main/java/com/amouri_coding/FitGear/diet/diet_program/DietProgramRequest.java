package com.amouri_coding.FitGear.diet.diet_program;

import com.amouri_coding.FitGear.diet.diet_day.DietDayRequest;
import lombok.Builder;

import java.util.List;

@Builder
public class DietProgramRequest {

    private String title;
    private String description;

    private List<DietDayRequest> days;
}
