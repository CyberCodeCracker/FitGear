package com.amouri_coding.FitGear.progress;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgressEntryRepository extends JpaRepository<ProgressEntry, Long> {

    List<ProgressEntry> findAllByClientIdOrderByEntryDateAsc(Long clientId);

    Page<ProgressEntry> findAllByClientIdOrderByEntryDateDesc(Long clientId, Pageable pageable);

    Optional<ProgressEntry> findByIdAndClientId(Long id, Long clientId);
}
