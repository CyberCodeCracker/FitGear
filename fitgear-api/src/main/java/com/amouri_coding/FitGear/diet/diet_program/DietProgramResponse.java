package com.amouri_coding.FitGear.diet.diet_program;

import com.amouri_coding.FitGear.diet.diet_day.DietDayResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class DietProgramResponse {

    private String title;
    private String description;

    private List<DietDayResponse> days;
}
