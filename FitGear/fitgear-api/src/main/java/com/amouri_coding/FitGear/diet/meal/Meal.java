package com.amouri_coding.FitGear.diet.meal;

import com.amouri_coding.FitGear.diet.diet_day.DietDay;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "TOTAL_CALORIES")
    private int totalCalories;

    @Column(name = "PROTEIN")
    private double protein;

    @Column(name = "CARBS")
    private double carbs;

    @Column(name = "FATS")
    private double fats;

    private LocalTime time;

    @ManyToOne
    @JoinColumn(name = "DIET_DAY_ID")
    private DietDay day;
}
