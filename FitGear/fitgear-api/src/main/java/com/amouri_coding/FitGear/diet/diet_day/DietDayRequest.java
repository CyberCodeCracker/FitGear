package com.amouri_coding.FitGear.diet.diet_day;

import com.amouri_coding.FitGear.common.DayOfWeek;
import com.amouri_coding.FitGear.diet.meal.MealRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class DietDayRequest {

    @NotNull(message = "Day is required")
    private DayOfWeek dayOfWeek;

    @NotEmpty(message = "Meals are required")
    private List<MealRequest> meals;

    @NotNull(message = "Calories can't be null")
    private int totalCaloriesInDay;

    @NotNull(message = "Protein can't be null")
    private double totalProteinInDay;

    @NotNull(message = "Carbs can't be null")
    private double totalCarbsInDay;

    @NotNull(message = "Fats can't be null")
    private double totalFatsInDay;
}
