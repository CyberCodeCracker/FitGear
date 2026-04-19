package com.amouri_coding.FitGear.diet.meal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Builder
public class MealResponse {

    private String description;

    private int calories;

    private double protein;
    private double carbs;
    private double fats;

    private LocalTime timeToEat;

}
