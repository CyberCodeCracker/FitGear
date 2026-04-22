package com.amouri_coding.FitGear.training.training_day;

import com.amouri_coding.FitGear.common.DayOfWeek;
import com.amouri_coding.FitGear.training.exercise.ExerciseResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class TrainingDayResponse {

    private Long id;
    private String title;
    private DayOfWeek dayOfWeek;
    private int estimatedBurnedCalories;
    private List<ExerciseResponse> exercises;
}
