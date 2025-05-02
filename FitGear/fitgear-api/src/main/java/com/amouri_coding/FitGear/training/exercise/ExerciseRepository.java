package com.amouri_coding.FitGear.training.exercise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    @Query("""
            SELECT ex.trainingDay.id FROM Exercise ex
            WHERE ex.id = :exerciseId
            """)
    Optional<Long> findDayIdByExerciseId(Long exerciseId);
}
