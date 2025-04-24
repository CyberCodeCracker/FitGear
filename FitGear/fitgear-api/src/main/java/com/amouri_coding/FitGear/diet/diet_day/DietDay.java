package com.amouri_coding.FitGear.diet.diet_day;

import com.amouri_coding.FitGear.common.DayOfWeek;
import com.amouri_coding.FitGear.diet.diet_program.DietProgram;
import com.amouri_coding.FitGear.diet.meal.Meal;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DietDay {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DAY")
    private DayOfWeek day;

    @ManyToOne
    @JoinColumn(name = "DIET_PROGRAM_ID")
    private DietProgram program;

    @OneToMany(mappedBy = "day")
    private List<Meal> meals;

    @Column(name = "TOTAL_CALS")
    private double totalCaloriesInDay;

    @Column(name = "TOTAL_PROTEIN")
    private double totalProteinInDay;

    @Column(name = "TOTAL_CARBS")
    private double totalCarbsInDay;
}
