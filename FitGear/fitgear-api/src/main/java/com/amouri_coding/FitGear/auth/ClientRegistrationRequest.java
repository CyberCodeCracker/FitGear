package com.amouri_coding.FitGear.auth;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientRegistrationRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "First name is required")
    private String lastName;

    @NotBlank(message = "First name is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password should be between 8 and 20 characters long")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    @Size(min = 8, max = 20, message = "Password should be between 8 and 20 characters long")
    private String passwordConfirm;

    @NotNull(message = "Height is required")
    @Min(value = 100, message = "Please input a valid height")
    private double height;

    @NotNull(message = "Weight is required")
    @Min(value = 40, message = "Please input a valid value")
    private double weight;

    @NotNull(message = "Body fat percentage is required")
    @Min(value = 0)
    @Max(value = 80)
    private double bodyFatPercentage;
}
