package com.amouri_coding.FitGear.diet.diet_day;

import com.amouri_coding.FitGear.common.DayOfWeek;
import com.amouri_coding.FitGear.diet.meal.MealRequest;
import lombok.Builder;

import java.util.List;

@Builder
public class DietDayRequest {

    private DayOfWeek day;

    private List<MealRequest> meals;

    private double totalCaloriesInDay;

    private double totalProteinInDay;

    private double totalCarbsInDay;
}
