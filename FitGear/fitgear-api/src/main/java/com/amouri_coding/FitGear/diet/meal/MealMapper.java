package com.amouri_coding.FitGear.diet.meal;

import org.springframework.stereotype.Service;

@Service
public class MealMapper {

    public Meal toMeal(MealRequest request) {

        Meal meal = Meal.builder()
                .calories(request.getCalories())
                .carbs(request.getCarbs())
                .fats(request.getFats())
                .protein(request.getProtein())
                .timeToEat(request.getTimeToEat())
                .description(request.getDescription())
                .build()
                ;
        return meal;
    }
}
