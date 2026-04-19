package com.amouri_coding.FitGear.diet.diet_day;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DietDayRepository extends JpaRepository<DietDay, Long> {

    @Query("""
            SELECT day.program.id FROM DietDay day
            WHERE day.id = :dayId
            """)
    Optional<Long> findDietProgramIdByDayId(Long dayId);

    @Query("""
            SELECT day.program.client.id FROM DietDay day
            WHERE day.id = :dayId
            """)
    Optional<Long> findClientIdByDayId(Long dayId);
}
