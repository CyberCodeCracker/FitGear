package com.amouri_coding.FitGear.user.client;

import com.amouri_coding.FitGear.diet.diet_program.DietProgram;
import com.amouri_coding.FitGear.training.training_program.TrainingProgram;
import com.amouri_coding.FitGear.user.User;
import com.amouri_coding.FitGear.user.coach.Coach;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "client")
@DiscriminatorValue("CLIENT")
@PrimaryKeyJoinColumn(name = "user_id")
@EntityListeners(AuditingEntityListener.class)
public class Client extends User {

    @Column(name = "HEIGHT", nullable = false)
    private double height;

    @Column(name = "WEIGHT", nullable = false)
    private double weight;

    @Column(name = "BODY_FAT_PERCENTAGE", nullable = false)
    private double bodyFatPercentage;

    @ManyToOne
    @JoinColumn(name = "coach_id")
    private Coach coach;

    @OneToOne
    @JoinColumn(name = "DIET_PROGRAM_ID")
    private DietProgram dietProgram;

    @OneToOne
    @JoinColumn(name = "TRAINING_PROGRAM_ID")
    private TrainingProgram trainingProgram;
}
