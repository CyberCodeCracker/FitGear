package com.amouri_coding.FitGear.user.me;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CoachSummaryResponse {

    private Long id;
    private String fullName;
    private double monthlyRate;
    private double rating;
    private String profilePicture;
}

