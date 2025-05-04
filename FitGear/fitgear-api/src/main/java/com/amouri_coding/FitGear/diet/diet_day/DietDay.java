package com.amouri_coding.FitGear.diet.diet_day;

import com.amouri_coding.FitGear.common.DayOfWeek;
import com.amouri_coding.FitGear.diet.diet_program.DietProgram;
import com.amouri_coding.FitGear.diet.meal.Meal;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DietDay {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DAY")
    @Enumerated(EnumType.STRING)
    private DayOfWeek day;

    @Column(name = "TOTAL_CALS")
    private int totalCaloriesInDay;

    @Column(name = "TOTAL_PROTEIN")
    private double totalProteinInDay;

    @Column(name = "TOTAL_CARBS")
    private double totalCarbsInDay;

    @Column(name = "TOTAL_FATS")
    private double totalFatsInDay;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "DIET_PROGRAM_ID")
    private DietProgram program;

    @OneToMany(mappedBy = "day", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Meal> meals;

    public int calculateTotalCalories() {
        this.totalCaloriesInDay = meals.stream()
                .mapToInt(meal -> meal.getCalories())
                .sum()
                ;
        return totalCaloriesInDay;
    }

    public double calculateTotalProtein() {
        this.totalProteinInDay = meals.stream()
                .mapToDouble(meal -> meal.getProtein())
                .sum()
                ;
        return totalProteinInDay;
    }

    public double calculateTotalCarbs() {
        this.totalCarbsInDay = meals.stream()
                .mapToDouble(meal -> meal.getCarbs())
                .sum()
                ;
        return totalCarbsInDay;
    }

    public double calculateTotalFats() {
        this.totalFatsInDay = meals.stream()
                .mapToDouble(meal -> meal.getFats())
                .sum()
                ;
        return totalFatsInDay;
    }

}
