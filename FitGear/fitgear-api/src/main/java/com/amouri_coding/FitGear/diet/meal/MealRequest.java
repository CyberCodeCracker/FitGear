package com.amouri_coding.FitGear.diet.meal;

import lombok.Builder;

@Builder
public class MealRequest {

    private String description;

    private int totalCalories;

    private double protein;
    private double fats;
    private double carbs;
}
