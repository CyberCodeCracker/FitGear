package com.amouri_coding.FitGear.progress;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressEntryResponse {

    private Long id;
    private String entryDate;
    private double weight;
    private double bodyFat;
    private Double muscleMass;
    private String notes;
    private String createdAt;
}
