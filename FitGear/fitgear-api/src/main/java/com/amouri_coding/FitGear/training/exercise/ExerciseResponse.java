package com.amouri_coding.FitGear.training.exercise;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ExerciseResponse {

    private String title;
    private String exerciseUrl;
    private int exerciseNumber;
    private int restTime;
    private int numberOfSets;
    private int numberOfReps;
}
