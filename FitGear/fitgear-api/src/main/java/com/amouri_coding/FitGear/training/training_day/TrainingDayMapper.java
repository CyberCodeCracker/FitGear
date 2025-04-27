package com.amouri_coding.FitGear.training.training_day;

import com.amouri_coding.FitGear.training.exercise.Exercise;
import com.amouri_coding.FitGear.training.exercise.ExerciseMapper;
import com.amouri_coding.FitGear.training.exercise.ExerciseResponse;
import com.amouri_coding.FitGear.training.training_program.TrainingProgram;
import com.amouri_coding.FitGear.training.training_program.TrainingProgramMapper;
import com.amouri_coding.FitGear.training.training_program.TrainingProgramRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingDayMapper {

    private final ExerciseMapper exerciseMapper;

    public TrainingDay toTrainingDay(TrainingDayRequest request) {

        TrainingDay trainingDay = TrainingDay.builder()
                .title(request.getTitle())
                .dayOfWeek(request.getDay())
                .estimatedBurnedCalories(request.getEstimatedBurnedCalories())
                .build();

        List<Exercise> mappedExercises = request.getExercises()
                .stream()
                .map(req -> exerciseMapper.toExercise(req))
                .toList();

        trainingDay.setExercises(mappedExercises);

        return trainingDay;
    }

    public TrainingDayResponse toTrainingDayResponse(TrainingDay trainingDay) {

        List<ExerciseResponse> mappedExercises = trainingDay.getExercises()
                .stream()
                .map(exerciseMapper::toExerciseResponse)
                .toList();

        return TrainingDayResponse.builder()
                .id(trainingDay.getId())
                .programId(trainingDay.getTrainingProgram().getId())
                .title(trainingDay.getTitle())
                .dayOfWeek(trainingDay.getDayOfWeek())
                .estimatedBurnedCalories(trainingDay.getEstimatedBurnedCalories())
                .exercises(mappedExercises)
                .build()
                ;
    }
}
