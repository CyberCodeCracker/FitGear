package com.amouri_coding.FitGear.user.client;

import com.amouri_coding.FitGear.user.coach.Coach;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private double height;
    private double weight;
    private double bodyFatPercentage;

}
