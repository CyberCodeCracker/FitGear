package com.amouri_coding.FitGear.diet.meal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Builder
@Getter
public class MealRequest {

    @NotEmpty(message = "Description can't be empty")
    private String description;

    @NotNull(message = "Calories can't be null")
    @Min(value = 1, message = "Calories must be at least 1")
    private int calories;

    private LocalTime timeToEat;

    @NotNull(message = "Protein can't be null")
    @Min(value = 0, message = "Protein can't be negative")
    private double protein;

    @NotNull(message = "Fats can't be null")
    @Min(value = 0, message = "Fats can't be negative")
    private double fats;

    @NotNull(message = "Carbs can't be null")
    @Min(value = 0, message = "Carbs can't be negative")
    private double carbs;
}
