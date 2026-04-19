package com.amouri_coding.FitGear.user.coach;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CoachRepository extends JpaRepository<Coach, Long> {

    Optional<Coach> findByEmail(String coachEmail);
    Coach findById(Integer coachId);

    @Query("""
            SELECT c
            FROM Coach c
            WHERE c.isVerified = true
              AND c.isAvailable = true
              AND (
                :q IS NULL OR :q = '' OR
                LOWER(c.firstName) LIKE LOWER(CONCAT(:q, '%')) OR
                LOWER(c.lastName) LIKE LOWER(CONCAT(:q, '%')) OR
                LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT(:q, '%'))
              )
            """)
    Page<Coach> searchAvailableVerified(Pageable pageable, String q);
}
