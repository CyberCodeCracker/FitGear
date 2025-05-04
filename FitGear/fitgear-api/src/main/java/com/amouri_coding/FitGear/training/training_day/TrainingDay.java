package com.amouri_coding.FitGear.training.training_day;

import com.amouri_coding.FitGear.common.DayOfWeek;
import com.amouri_coding.FitGear.training.exercise.Exercise;
import com.amouri_coding.FitGear.training.training_program.TrainingProgram;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class TrainingDay {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PROGRAM_ID")
    private TrainingProgram trainingProgram;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "DAY")
    private DayOfWeek dayOfWeek;

    @OneToMany(mappedBy = "trainingDay", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Exercise> exercises;

    @Column(name = "BURNED_CALORIES", nullable = false)
    private int estimatedBurnedCalories;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

}
