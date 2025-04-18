package com.amouri_coding.FitGear.user.coach;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoachRepository extends JpaRepository<Coach, Long> {

    Optional<Coach> findByEmail(String coachEmail);
    Coach findById(Integer coachId);
}
