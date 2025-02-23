package com.amouri_coding.FitGear.auth;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class CoachRegistrationRequest {

    @NotBlank(message = "First name is required")
    private String coachFirstName;

    @NotBlank(message = "First name is required")
    private String coachLastName;

    @NotBlank(message = "First name is required")
    @Email(message = "Invalid email format")
    private String coachEmail;

    private String coachProfilePicture;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password should be at least 8 characters long")
    private String coachPassword;

    @NotBlank(message = "Password confirmation is required")
    @Size(min = 8, message = "Password should be at least 8 characters long")
    private String coachPasswordConfirm;

    @NotBlank(message = "Password confirmation is required")
    @Pattern(regexp = "^[0-9]+$", message = "Phone number must contain only digits")
    @Max(value = 8, message = "Phone number is too long")
    private String coachPhoneNumber;

    @NotBlank(message = "Description is required")
    private String coachDescription;

    @Min(value = 0, message = "Years of experience msut be positive")
    private int yearsOfExperience;

    @NotNull(message = "specialties are required")
    private List<Long> specialtiesIds;

    @NotNull(message = "certifications are required")
    private List<Long> certificationsIds;

    @Positive(message = "Monthly rate must be a positive number")
    private double monthlyRate;

    private String profilePicture;


}
