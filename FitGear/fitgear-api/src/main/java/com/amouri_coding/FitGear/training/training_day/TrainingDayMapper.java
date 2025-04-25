package com.amouri_coding.FitGear.training.training_day;

import com.amouri_coding.FitGear.training.exercise.Exercise;
import com.amouri_coding.FitGear.training.exercise.ExerciseMapper;
import com.amouri_coding.FitGear.training.exercise.ExerciseRepository;
import com.amouri_coding.FitGear.training.training_program.TrainingProgram;
import com.amouri_coding.FitGear.training.training_program.TrainingProgramRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingDayMapper {

    private final TrainingProgramRepository trainingProgramRepository;
    private final ExerciseMapper exerciseMapper;

    public TrainingDay toTrainingDay(TrainingDayRequest request, TrainingProgram program) {

        TrainingDay trainingDay = TrainingDay.builder()
                .program(program)
                .title(request.getTitle())
                .dayOfWeek(request.getDay())
                .estimatedBurnedCalories(request.getEstimatedBurnedCalories())
                .build()
                ;

        List<Exercise> mappedExercises = request.getExercises()
                .stream()
                .map(req -> exerciseMapper.toExercise(req, trainingDay))
                .toList()
                ;

        trainingDay.setExercises(mappedExercises);

        return trainingDay;


    }
}
