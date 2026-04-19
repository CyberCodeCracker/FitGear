package com.amouri_coding.FitGear.auth;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvitationRequest {

    @Size(max = 500, message = "Message should not exceed 500 characters")
    private String message;
}
