package com.amouri_coding.FitGear.user.me;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MeResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> roles;
    private String userType;

    private CoachSummaryResponse coach;
    private Double height;
    private Double weight;
    private Double bodyFatPercentage;

    private Double rating;
    private Double monthlyRate;
    private String description;
    private String phoneNumber;
    private Integer yearsOfExperience;
    private Boolean isAvailable;
}

