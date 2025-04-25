package com.amouri_coding.FitGear.training.training_program;

import com.amouri_coding.FitGear.training.training_day.TrainingDay;
import com.amouri_coding.FitGear.training.training_day.TrainingDayMapper;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.coach.Coach;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingProgramMapper {

    private final TrainingDayMapper trainingDayMapper;

    public TrainingProgram toTrainingProgram(TrainingProgramRequest request, Client client, Coach coach) {

        TrainingProgram trainingProgram = TrainingProgram.builder()
                .client(client)
                .coach(coach)
                .build()
                ;

        List<TrainingDay> mappedTrainingDays = request.getTrainingDays()
                .stream()
                .map(req -> trainingDayMapper.toTrainingDay(req, trainingProgram))
                .toList()
                ;

        trainingProgram.setTrainingDays(mappedTrainingDays);

        return trainingProgram;
    }
}
