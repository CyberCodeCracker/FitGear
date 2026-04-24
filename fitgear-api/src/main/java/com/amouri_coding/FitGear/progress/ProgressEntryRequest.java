package com.amouri_coding.FitGear.progress;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressEntryRequest {

    @NotNull(message = "Date is required")
    private String entryDate; // "yyyy-MM-dd"

    @NotNull(message = "Weight is required")
    @Min(value = 20, message = "Weight must be at least 20 kg")
    private Double weight;

    @NotNull(message = "Body fat is required")
    @Min(value = 0, message = "Body fat can't be negative")
    @Max(value = 80, message = "Body fat can't exceed 80%")
    private Double bodyFat;

    @Min(value = 0, message = "Muscle mass can't be negative")
    private Double muscleMass;

    @Size(max = 500, message = "Notes can't exceed 500 characters")
    private String notes;
}
