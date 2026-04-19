package com.amouri_coding.FitGear.training.training_day;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface TrainingDayRepository extends JpaRepository<TrainingDay, Long> {

    @Query("""
            SELECT day.trainingProgram.id FROM TrainingDay day
            WHERE day.id = :dayId
            """)
    Optional<Long> findProgramIdByDayId(Long dayId);
}
