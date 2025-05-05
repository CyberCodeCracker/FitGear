package com.amouri_coding.FitGear.diet.diet_day;

import com.amouri_coding.FitGear.diet.meal.Meal;
import com.amouri_coding.FitGear.diet.meal.MealMapper;
import com.amouri_coding.FitGear.diet.meal.MealResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DietDayMapper {

    private final MealMapper mealMapper;

    public DietDay toDietDay(DietDayRequest request) {

        DietDay dietDay = DietDay.builder()
                .day(request.getDay())
                .totalCarbsInDay(request.getTotalCarbsInDay())
                .totalCaloriesInDay(request.getTotalCaloriesInDay())
                .totalProteinInDay(request.getTotalProteinInDay())
                .build()
                ;

        List<Meal> mappedMeals = request.getMeals()
                .stream()
                .map(mealRequest -> mealMapper.toMeal(mealRequest))
                .peek(meal -> meal.setDay(dietDay))
                .collect(Collectors.toCollection(ArrayList::new))
                ;

        dietDay.setMeals(mappedMeals);
        return dietDay;
    }

    public DietDayResponse toDietDayResponse(DietDay dietDay) {

        List<MealResponse> mealResponses = dietDay.getMeals()
                .stream()
                .map(meal -> mealMapper.toMealResponse(meal))
                .collect(Collectors.toCollection(ArrayList::new))
                ;

        DietDayResponse dayResponse = DietDayResponse.builder()
                .day(dietDay.getDay())
                .totalCarbsInDay(dietDay.getTotalCarbsInDay())
                .totalCaloriesInDay(dietDay.getTotalCaloriesInDay())
                .totalProteinInDay(dietDay.getTotalProteinInDay())
                .totalFatsInDay(dietDay.getTotalFatsInDay())
                .meals(mealResponses)
                .build()
                ;
        return dayResponse;
    }
}
