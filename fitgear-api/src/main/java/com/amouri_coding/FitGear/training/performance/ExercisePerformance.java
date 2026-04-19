package com.amouri_coding.FitGear.training.performance;

import com.amouri_coding.FitGear.training.exercise.Exercise;
import com.amouri_coding.FitGear.user.client.Client;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class ExercisePerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CLIENT_ID", nullable = false)
    private Client client;

    @ManyToOne(optional = false)
    @JoinColumn(name = "EXERCISE_ID", nullable = false)
    private Exercise exercise;

    @Column(name = "PERFORMED_AT", nullable = false)
    private LocalDate performedAt;

    @Column(name = "NUMBER_OF_SETS", nullable = false)
    private int numberOfSets;

    @Column(name = "NUMBER_OF_REPS", nullable = false)
    private int numberOfReps;

    @Column(name = "WEIGHT", nullable = false)
    private double weight;

    @Column(name = "NOTES", length = 1024)
    private String notes;

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

