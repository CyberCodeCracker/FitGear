package com.amouri_coding.FitGear.user.coach;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CoachDetailResponse {

    private Long id;
    private String fullName;
    private String description;
    private int yearsOfExperience;
    private double monthlyRate;
    private double rating;
    private long reviewCount;
    private String phoneNumber;
    private String profilePicture;
    private boolean available;
    private List<TestimonialResponse> testimonials;
}
