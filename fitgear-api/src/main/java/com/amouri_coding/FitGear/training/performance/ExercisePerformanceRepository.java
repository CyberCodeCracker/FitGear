package com.amouri_coding.FitGear.training.performance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExercisePerformanceRepository extends JpaRepository<ExercisePerformance, Long> {

    @Query("""
            SELECT p
            FROM ExercisePerformance p
            WHERE p.client.id = :clientId
              AND p.exercise.id = :exerciseId
            ORDER BY p.performedAt ASC, p.id ASC
            """)
    List<ExercisePerformance> findForClientAndExercise(Long clientId, Long exerciseId);
}

