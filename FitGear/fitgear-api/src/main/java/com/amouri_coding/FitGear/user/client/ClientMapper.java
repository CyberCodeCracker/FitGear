package com.amouri_coding.FitGear.user.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientMapper {

    public ClientResponse toClientResponse(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .height(client.getHeight())
                .weight(client.getWeight())
                .bodyFatPercentage(client.getBodyFatPercentage())
                .build()
                ;
    }
}
