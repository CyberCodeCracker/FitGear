package com.amouri_coding.FitGear.training.training_program;

import com.amouri_coding.FitGear.training.training_day.TrainingDay;
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

}
