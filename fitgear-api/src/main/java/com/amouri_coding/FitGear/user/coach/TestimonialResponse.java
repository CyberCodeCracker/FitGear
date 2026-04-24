package com.amouri_coding.FitGear.user.coach;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialResponse {

    private Long id;
    private String clientName;
    private String clientInitials;
    private int rating;
    private String comment;
    private String createdAt;
}
