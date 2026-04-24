package com.amouri_coding.FitGear.user.me;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Min(value = 100, message = "Please input a valid height")
    private Double height;

    @Min(value = 40, message = "Please input a valid weight")
    private Double weight;

    @Min(value = 0) @Max(value = 80)
    private Double bodyFatPercentage;

    private String description;

    @Pattern(regexp = "^[0-9]{8}$", message = "Phone number must be 8 digits")
    private String phoneNumber;

    @Min(value = 0, message = "Years of experience must be positive")
    private Integer yearsOfExperience;

    @Positive(message = "Monthly rate must be positive")
    private Double monthlyRate;

    private Boolean isAvailable;
}
