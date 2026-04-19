package com.amouri_coding.FitGear.user.client;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClientCoachResponse {

    private Long id;
    private String fullName;
    private String description;
    private double monthlyRate;
    private double rating;
    private String profilePicture;
}

