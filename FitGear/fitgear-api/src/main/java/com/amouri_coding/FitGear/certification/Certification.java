package com.amouri_coding.FitGear.certification;

import com.amouri_coding.FitGear.coach.Coach;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String certificateUrl;

    @ManyToMany(mappedBy = "certifications")
    private List<Coach> coaches;
}
