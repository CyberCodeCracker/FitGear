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

    @Query("""
            SELECT COUNT(ex) > 0
            FROM Exercise ex
            JOIN ex.trainingDay day
            JOIN day.trainingProgram program
            WHERE ex.id = :exerciseId
            AND program.client.id = :clientId
            """)
    boolean existsByExerciseIdAndClientId(Long exerciseId, Long clientId);
}
