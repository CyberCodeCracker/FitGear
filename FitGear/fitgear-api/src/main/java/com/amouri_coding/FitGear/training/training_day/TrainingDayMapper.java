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
    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;

    public TrainingDay toTrainingDay(TrainingDayRequest request) {

        TrainingProgram program = trainingProgramRepository.findById(request.getProgramId())
                .orElseThrow(() -> new EntityNotFoundException("Program not found"));

        List<Exercise> mappedExercises = request.getExercises()
                .stream()
                .map(exerciseMapper::toExercise)
                .toList()
                ;

        return TrainingDay.builder()
                .program(program)
                .title(request.getTitle())
                .dayOfWeek(request.getDay())
                .estimatedBurnedCalories(request.getEstimatedBurnedCalories())
                .build()
                ;
    }
}
