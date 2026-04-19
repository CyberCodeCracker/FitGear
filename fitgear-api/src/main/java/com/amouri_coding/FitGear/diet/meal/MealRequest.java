package com.amouri_coding.FitGear.diet.meal;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Builder
@Getter
public class MealRequest {

    @NotEmpty(message = "Description can't be null")
    private String description;


    @NotNull(message = "Calories can't be null")
    private int calories;

    private LocalTime timeToEat;

    @NotNull(message = "Protein can't be null")
    private double protein;

    @NotNull(message = "Fats can't be null")
    private double fats;

    @NotNull(message = "Carbs can't be null")
    private double carbs;
}
