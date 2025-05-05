package com.amouri_coding.FitGear.diet.diet_day;

import com.amouri_coding.FitGear.common.DayOfWeek;
import com.amouri_coding.FitGear.diet.meal.MealResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class DietDayResponse {

    private DayOfWeek dayOfWeek;

    private int totalCaloriesInDay;

    private double totalProteinInDay;
    private double totalCarbsInDay;
    private double totalFatsInDay;

    private List<MealResponse> meals;
}
