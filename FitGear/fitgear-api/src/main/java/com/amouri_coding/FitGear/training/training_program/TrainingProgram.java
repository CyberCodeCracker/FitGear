package com.amouri_coding.FitGear.training.training_program;

import com.amouri_coding.FitGear.training.training_day.TrainingDay;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.coach.Coach;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class TrainingProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID")
    private Long id;

    @OneToMany(mappedBy = "program")
    private List<TrainingDay> trainingDays;

    @OneToOne
    private Client client;

    @ManyToOne
    @JoinColumn(name = "COACH_ID")
    private Coach coach;

}
