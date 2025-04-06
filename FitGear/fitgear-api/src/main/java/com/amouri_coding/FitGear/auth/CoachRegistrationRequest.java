package com.amouri_coding.FitGear.auth;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoachRegistrationRequest {

    @NotBlank(message = "First name is required")
    private String coachFirstName;

    @NotBlank(message = "First name is required")
    private String coachLastName;

    @NotBlank(message = "First name is required")
    @Email(message = "Invalid email format")
    private String coachEmail;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password should be between 8 and 20 characters long")
    private String coachPassword;

    @NotBlank(message = "Password confirmation is required")
    @Size(min = 8, max = 20, message = "Password should be between 8 and 20 characters long")
    private String coachPasswordConfirm;

    @NotBlank(message = "Password confirmation is required")
    @Pattern(regexp = "^[0-9]+$", message = "Phone number must contain only digits")
    @Max(value = 99999999, message = "Phone number is not valid")
    @Size(max = 8, message = "Phone number is too long")
    @Column(nullable = false)
    private String phoneNumber;

    @NotBlank(message = "Description is required")
    @Column(length = 2048, nullable = false)
    private String description;

    @Min(value = 0, message = "Years of experience must be positive")
    @Column(nullable = false)
    private int yearsOfExperience;

    @Positive(message = "Monthly rate must be a positive number")
    @Column(nullable = false)
    private double monthlyRate;

}
