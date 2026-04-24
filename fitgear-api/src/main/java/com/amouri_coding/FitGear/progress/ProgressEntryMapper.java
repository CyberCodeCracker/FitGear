package com.amouri_coding.FitGear.progress;

import org.springframework.stereotype.Component;

@Component
public class ProgressEntryMapper {

    public ProgressEntryResponse toResponse(ProgressEntry entry) {
        return ProgressEntryResponse.builder()
                .id(entry.getId())
                .entryDate(entry.getEntryDate().toString())
                .weight(entry.getWeight())
                .bodyFat(entry.getBodyFat())
                .muscleMass(entry.getMuscleMass())
                .notes(entry.getNotes())
                .createdAt(entry.getCreatedAt() != null ? entry.getCreatedAt().toString() : null)
                .build();
    }
}
