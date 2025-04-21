package com.amouri_coding.FitGear.training.training_day;

import com.amouri_coding.FitGear.training.exercise.Exercise;
import com.amouri_coding.FitGear.training.training_program.TrainingProgram;
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
public class TrainingDay {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PROGRAM_ID")
    private TrainingProgram program;

    @OneToMany(mappedBy = "day")
    private List<Exercise> exercises;
}
