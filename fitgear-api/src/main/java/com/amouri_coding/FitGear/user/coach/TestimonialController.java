package com.amouri_coding.FitGear.user.coach;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coaches/{coachId}/testimonials")
@RequiredArgsConstructor
@Tag(name = "Testimonials")
public class TestimonialController {

    private final TestimonialService service;

    @GetMapping
    public ResponseEntity<List<TestimonialResponse>> getTestimonials(
            @PathVariable Long coachId
    ) {
        return ResponseEntity.ok(service.getTestimonials(coachId));
    }

    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @PostMapping
    public ResponseEntity<TestimonialResponse> createOrUpdate(
            @PathVariable Long coachId,
            @RequestBody @Valid TestimonialRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createOrUpdate(coachId, request, authentication));
    }

    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @DeleteMapping("/{testimonialId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long coachId,
            @PathVariable Long testimonialId,
            Authentication authentication
    ) {
        service.deleteTestimonial(coachId, testimonialId, authentication);
        return ResponseEntity.noContent().build();
    }
}
