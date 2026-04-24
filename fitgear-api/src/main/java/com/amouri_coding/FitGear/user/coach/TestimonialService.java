package com.amouri_coding.FitGear.user.coach;

import com.amouri_coding.FitGear.common.SecurityUtils;
import com.amouri_coding.FitGear.user.client.Client;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TestimonialService {

    private final TestimonialRepository testimonialRepository;
    private final CoachRepository coachRepository;

    public List<TestimonialResponse> getTestimonials(Long coachId) {
        if (!coachRepository.existsById(coachId)) {
            throw new EntityNotFoundException("Coach not found");
        }
        return testimonialRepository.findAllByCoachIdOrderByCreatedAtDesc(coachId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TestimonialResponse createOrUpdate(Long coachId, TestimonialRequest request, Authentication authentication) {
        Client client = SecurityUtils.getAuthenticatedClient(authentication);

        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new EntityNotFoundException("Coach not found"));

        // One review per client per coach — update if exists
        ClientTestimonial testimonial = testimonialRepository
                .findByClientIdAndCoachId(client.getId(), coachId)
                .orElse(ClientTestimonial.builder()
                        .client(client)
                        .coach(coach)
                        .build());

        testimonial.setRating(request.getRating());
        testimonial.setComment(request.getComment());

        ClientTestimonial saved = testimonialRepository.save(testimonial);

        // Recalculate coach average rating
        Double avg = testimonialRepository.findAverageRatingByCoachId(coachId);
        coach.setRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        coachRepository.save(coach);

        log.info("Testimonial by client {} for coach {} — rating: {}", client.getId(), coachId, request.getRating());
        return toResponse(saved);
    }

    public void deleteTestimonial(Long coachId, Long testimonialId, Authentication authentication) {
        Client client = SecurityUtils.getAuthenticatedClient(authentication);

        ClientTestimonial testimonial = testimonialRepository.findById(testimonialId)
                .orElseThrow(() -> new EntityNotFoundException("Testimonial not found"));

        if (!testimonial.getClient().getId().equals(client.getId())) {
            throw new IllegalStateException("You can only delete your own testimonial");
        }

        testimonialRepository.delete(testimonial);

        // Recalculate coach average rating
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new EntityNotFoundException("Coach not found"));
        Double avg = testimonialRepository.findAverageRatingByCoachId(coachId);
        coach.setRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        coachRepository.save(coach);
    }

    private TestimonialResponse toResponse(ClientTestimonial t) {
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
