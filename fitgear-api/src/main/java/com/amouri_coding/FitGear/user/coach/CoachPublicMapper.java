package com.amouri_coding.FitGear.user.coach;

import org.springframework.stereotype.Service;

@Service
public class CoachPublicMapper {

    public CoachCardResponse toCoachCard(Coach coach) {
        return CoachCardResponse.builder()
                .id(coach.getId())
                .fullName(coach.getName())
                .description(coach.getDescription())
                .yearsOfExperience(coach.getYearsOfExperience())
                .monthlyRate(coach.getMonthlyRate())
                .rating(coach.getRating())
                .profilePicture(coach.getProfilePicture())
                .build();
    }

    public CoachDetailResponse toCoachDetail(Coach coach) {
        return CoachDetailResponse.builder()
                .id(coach.getId())
                .fullName(coach.getName())
                .description(coach.getDescription())
                .yearsOfExperience(coach.getYearsOfExperience())
                .monthlyRate(coach.getMonthlyRate())
                .rating(coach.getRating())
                .phoneNumber(coach.getPhoneNumber())
                .profilePicture(coach.getProfilePicture())
                .available(coach.isAvailable())
                .build();
    }
}

