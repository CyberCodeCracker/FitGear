package com.amouri_coding.FitGear.training.training_program;

import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.coach.Coach;
import org.springframework.stereotype.Service;

@Service
public class TrainingProgramMapper {

    public TrainingProgram toTrainingProgram(TrainingProgramRequest request, Client client, Coach coach) {
        return TrainingProgram.builder()
                .client(client)
                .coach(coach)
                .title(request.getTitle())
                .description(request.getDescription())
                .trainingDays(null)
                .build()
                ;
    }
}
