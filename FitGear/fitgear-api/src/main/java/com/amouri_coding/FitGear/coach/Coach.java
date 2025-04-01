package com.amouri_coding.FitGear.coach;

import com.amouri_coding.FitGear.certification.Certification;
import com.amouri_coding.FitGear.specialty.Specialty;
import com.amouri_coding.FitGear.user.Client;
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

    @Column(name = "description")
    private String description;

    @Column(name = "years_of_experience")
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

    @Column(name = "monthly_rate")
    private double monthlyRate;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "is_available")
    private boolean isAvailable;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "is_verified")
    private boolean isVerified;

    @Column(name = "rating")
    private double rating;

    @OneToMany
    @JoinColumn(name = "coach_id")
    private List<Client> clients;
}