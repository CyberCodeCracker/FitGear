package com.amouri_coding.FitGear.training.training_program;

import com.amouri_coding.FitGear.training.training_day.TrainingDay;
import com.amouri_coding.FitGear.training.training_day.TrainingDayMapper;
import com.amouri_coding.FitGear.training.training_day.TrainingDayResponse;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.coach.Coach;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingProgramMapper {

    private final TrainingDayMapper trainingDayMapper;
    private final EntityManager entityManager;

    public TrainingProgram toTrainingProgram(TrainingProgramRequest request, Long clientId, Long coachId) {

        Client client = entityManager.getReference(Client.class, clientId);
        Coach coach = entityManager.getReference(Coach.class, coachId);

        TrainingProgram trainingProgram = TrainingProgram.builder()
                .client(client)
                .coach(coach)
                .build()
                ;

        List<TrainingDay> mappedTrainingDays = request.getTrainingDays()
                .stream()
                .map(req -> trainingDayMapper.toTrainingDay(req))
                .peek(trainingDay -> trainingDay.setTrainingProgram(trainingProgram))
                .collect(Collectors.toCollection(ArrayList::new))
                ;

        trainingProgram.setTrainingDays(mappedTrainingDays);
        client.setTrainingProgram(trainingProgram);
        return trainingProgram;
    }

    public TrainingProgramResponse toTrainingProgramResponse(TrainingProgram trainingProgram) {

        List<TrainingDayResponse> mappedTrainingDays = trainingProgram.getTrainingDays()
                .stream()
                .map(trainingDayMapper::toTrainingDayResponse)
                .collect(Collectors.toCollection(ArrayList::new))
                ;

        return TrainingProgramResponse.builder()
                .id(trainingProgram.getId())
                .clientId(trainingProgram.getClient().getId())
                .coachId(trainingProgram.getCoach().getId())
                .trainingDays(mappedTrainingDays)
                .build()
                ;
    }
}
