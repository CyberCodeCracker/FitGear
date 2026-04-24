package com.amouri_coding.FitGear.user.coach;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TestimonialRepository extends JpaRepository<ClientTestimonial, Long> {

    List<ClientTestimonial> findAllByCoachIdOrderByCreatedAtDesc(Long coachId);

    Optional<ClientTestimonial> findByClientIdAndCoachId(Long clientId, Long coachId);

    @Query("SELECT AVG(t.rating) FROM ClientTestimonial t WHERE t.coach.id = :coachId")
    Double findAverageRatingByCoachId(Long coachId);

    long countByCoachId(Long coachId);
}
