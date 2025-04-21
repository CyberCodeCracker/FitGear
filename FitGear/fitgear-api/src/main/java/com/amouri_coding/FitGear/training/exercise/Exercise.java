package com.amouri_coding.FitGear.training.exercise;

import com.amouri_coding.FitGear.training.training_day.TrainingDay;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NUMBER_OF_SETS")
    private int numberOfSets;

    @Column(name = "NUMBER_OF_REPS")
    private int numberOfReps;

    @Column(name = "REST_TIME")
    private String restTime;

    @ManyToOne
    @JoinColumn(name = "TRAINING_DAY_ID")
    private TrainingDay trainingDay;
}
