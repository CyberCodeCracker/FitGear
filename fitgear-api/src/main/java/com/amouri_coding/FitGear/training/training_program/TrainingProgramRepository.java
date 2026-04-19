package com.amouri_coding.FitGear.training.training_program;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TrainingProgramRepository extends JpaRepository<TrainingProgram, Long> {

    @Query("""
            SELECT program.client.id FROM TrainingProgram program
            WHERE program.id = :programId
            """)
    Optional<Long> findClientIdByProgramId(Long programId);
}
