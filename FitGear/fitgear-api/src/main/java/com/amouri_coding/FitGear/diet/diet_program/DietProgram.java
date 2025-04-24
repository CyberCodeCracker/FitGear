package com.amouri_coding.FitGear.diet.diet_program;

import com.amouri_coding.FitGear.diet.diet_day.DietDay;
import com.amouri_coding.FitGear.user.client.Client;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class DietProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "DESCRIPTION")
    private String description;

    @OneToMany(mappedBy = "program")
    private List<DietDay> days;

    @OneToOne
    @JoinColumn(name = "CLIENT_ID")
    private Client client;
}
