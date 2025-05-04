package com.amouri_coding.FitGear.diet.diet_program;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DietProgramRepository extends JpaRepository<DietProgram, Long> {

    @Query("""
            SELECT program.client.id FROM DietProgram program
            WHERE program.id = :programId
            """)
    Optional<Long> findClientIdByDietProgramId(Long programId);
}
