package com.amouri_coding.FitGear.diet.meal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MealRepository extends JpaRepository<Meal, Long> {

    @Query("""
            SELECT meal.day.id FROM Meal meal
            WHERE meal.id = :mealId
            """)
    Optional<Long> findDietDayIdFromMealId(Long mealId);

    @Query("""
            SELECT meal.day.program.id FROM Meal meal
            WHERE meal.id = :mealId
            """)
    Optional<Long> findDietProgramIdFromMealId(Long mealId);

    @Query("""
            SELECT meal.day.program.client.id FROM Meal meal
            WHERE meal.id = :mealId
            """)
    Optional<Long> findClientIdByMealId(Long mealId);
}
