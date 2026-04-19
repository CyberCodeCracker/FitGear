package com.amouri_coding.FitGear.user.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class ClientMapper {

    public ClientResponse toClientResponse(Client client) {
        Long trainingProgramId = client.getTrainingProgram() != null
                ? client.getTrainingProgram().getId() : null;
        Long dietProgramId = client.getDietProgram() != null
                ? client.getDietProgram().getId() : null;

        return ClientResponse.builder()
                .id(client.getId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .height(client.getHeight())
                .weight(client.getWeight())
                .bodyFatPercentage(client.getBodyFatPercentage())
                .trainingProgramId(trainingProgramId)
                .dietProgramId(dietProgramId)
                .build();
    }
}
