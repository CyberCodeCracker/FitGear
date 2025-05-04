package com.amouri_coding.FitGear.user.coach;

import com.amouri_coding.FitGear.certification.Certification;
import com.amouri_coding.FitGear.diet.diet_program.DietProgram;
import com.amouri_coding.FitGear.specialty.Specialty;
import com.amouri_coding.FitGear.training.training_program.TrainingProgram;
import com.amouri_coding.FitGear.user.client.Client;
import com.amouri_coding.FitGear.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "coach")
@PrimaryKeyJoinColumn(name = "user_id")
@DiscriminatorValue(value = "COACH")
@EntityListeners(AuditingEntityListener.class)
public class Coach extends User {

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "YEARS_OF_EXPERIENCE")
    private int yearsOfExperience;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "coach_specialties",
            joinColumns = @JoinColumn(name = "coach_id"),
            inverseJoinColumns = @JoinColumn(name = "specialty_id")
    )
    private List<Specialty> specialties;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "coach_certifications",
            joinColumns = @JoinColumn(name = "coach_id"),
            inverseJoinColumns = @JoinColumn(name = "certification_id")
    )
    private List<Certification> certifications;

    @Column(name = "MONTHLY_RATE")
    private double monthlyRate;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "IS_AVAILABLE")
    private boolean isAvailable;

    @Column(name = "PROFILE_PICTURE")
    private String profilePicture;

    @Column(name = "IS_VERIFIED")
    private boolean isVerified;

    @Column(name = "RATING")
    private double rating;

    @OneToMany(mappedBy = "coach")
    private List<Client> clients;

    @OneToMany(mappedBy = "coach")
    private List<TrainingProgram> trainingPrograms;

    @OneToMany(mappedBy = "coach")
    private List<DietProgram> dietPrograms;
}