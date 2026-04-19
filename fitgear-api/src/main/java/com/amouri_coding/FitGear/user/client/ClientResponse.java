package com.amouri_coding.FitGear.user.client;

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

    /** null when the client has no training program assigned yet */
    private Long trainingProgramId;

    /** null when the client has no diet program assigned yet */
    private Long dietProgramId;
}
