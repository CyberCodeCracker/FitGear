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
    private String userType; // CLIENT | COACH

    // Client-only
    private CoachSummaryResponse coach;
}

