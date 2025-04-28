package com.amouri_coding.FitGear.training.exercise;

import com.amouri_coding.FitGear.training.training_day.TrainingDay;
import com.amouri_coding.FitGear.training.training_day.TrainingDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExerciseMapper {

    private final TrainingDayRepository trainingDayRepository;

    public Exercise toExercise(ExerciseRequest request) {

        return Exercise.builder()
                .title(request.getTitle())
                .number(request.getExerciseNumber())
                .exerciseUrl(request.getExerciseUrl())
                .restTime(request.getRestTime())
                .numberOfReps(request.getNumberOfReps())
                .numberOfSets(request.getNumberOfSets())
                .build()
                ;
    }

    public ExerciseResponse toExerciseResponse(Exercise exercise) {
        return ExerciseResponse.builder()
                .id(exercise.getId())
                .trainingDayId(exercise.getTrainingDay().getId())
                .title(exercise.getTitle())
                .exerciseNumber(exercise.getNumber())
                .exerciseUrl(exercise.getExerciseUrl())
                .restTime(exercise.getRestTime())
                .numberOfSets(exercise.getNumberOfSets())
                .numberOfReps(exercise.getNumberOfReps())
                .build()
                ;
    }
}
