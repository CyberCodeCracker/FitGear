package com.amouri_coding.FitGear.training.exercise;

import com.amouri_coding.FitGear.training.training_day.TrainingDay;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "NUMBER")
    private int number;

    @Column(name = "EXERCISE_URL")
    private String exerciseUrl;

    @Column(name = "NUMBER_OF_SETS")
    private int numberOfSets;

    @Column(name = "NUMBER_OF_REPS")
    private int numberOfReps;

    @Column(name = "REST_TIME")
    private int restTime;

    @ManyToOne
    @JoinColumn(name = "TRAINING_DAY_ID")
    private TrainingDay trainingDay;
}
