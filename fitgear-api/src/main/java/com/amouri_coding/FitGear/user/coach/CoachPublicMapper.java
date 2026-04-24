package com.amouri_coding.FitGear.user.coach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoachPublicMapper {

    private final TestimonialRepository testimonialRepository;

    public CoachCardResponse toCoachCard(Coach coach) {
        return CoachCardResponse.builder()
                .id(coach.getId())
                .fullName(coach.getName())
                .description(coach.getDescription())
                .yearsOfExperience(coach.getYearsOfExperience())
                .monthlyRate(coach.getMonthlyRate())
                .rating(coach.getRating())
                .reviewCount(testimonialRepository.countByCoachId(coach.getId()))
                .profilePicture(coach.getProfilePicture())
                .build();
    }

    public CoachDetailResponse toCoachDetail(Coach coach) {
        List<TestimonialResponse> testimonials = testimonialRepository
                .findAllByCoachIdOrderByCreatedAtDesc(coach.getId())
                .stream()
                .map(this::toTestimonialResponse)
                .toList();

        return CoachDetailResponse.builder()
                .id(coach.getId())
                .fullName(coach.getName())
                .description(coach.getDescription())
                .yearsOfExperience(coach.getYearsOfExperience())
                .monthlyRate(coach.getMonthlyRate())
                .rating(coach.getRating())
                .reviewCount(testimonials.size())
                .phoneNumber(coach.getPhoneNumber())
                .profilePicture(coach.getProfilePicture())
                .available(coach.isAvailable())
                .testimonials(testimonials)
                .build();
    }

    private TestimonialResponse toTestimonialResponse(ClientTestimonial t) {
        String firstName = t.getClient().getFirstName();
        String lastName = t.getClient().getLastName();
        return TestimonialResponse.builder()
                .id(t.getId())
                .clientName(firstName + " " + lastName)
                .clientInitials(("" + firstName.charAt(0) + lastName.charAt(0)).toUpperCase())
                .rating(t.getRating())
                .comment(t.getComment())
                .createdAt(t.getCreatedAt() != null ? t.getCreatedAt().toString() : null)
                .build();
    }
}
