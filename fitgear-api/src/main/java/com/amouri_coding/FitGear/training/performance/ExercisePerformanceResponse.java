package com.amouri_coding.FitGear.training.performance;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class ExercisePerformanceResponse {

    private Long id;
    private Long exerciseId;
    private String exerciseTitle;
    private LocalDate performedAt;
    private int numberOfSets;
    private int numberOfReps;
    private double weight;
    private String notes;
}

