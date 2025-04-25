package com.amouri_coding.FitGear.training.exercise;

import com.amouri_coding.FitGear.training.training_day.TrainingDay;
import com.amouri_coding.FitGear.training.training_day.TrainingDayRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExerciseMapper {

    private final TrainingDayRepository trainingDayRepository;

    public Exercise toExercise(ExerciseRequest request, TrainingDay trainingDay) {

        return Exercise.builder()
                .title(request.getTitle())
                .day(trainingDay)
                .exerciseUrl(request.getExerciseUrl())
                .restTime(request.getRestTime())
                .numberOfReps(request.getNumberOfReps())
                .numberOfSets(request.getNumberOfSets())
                .build()
                ;
    }
}
