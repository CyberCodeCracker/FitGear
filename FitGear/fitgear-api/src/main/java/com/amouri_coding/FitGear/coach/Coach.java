package com.amouri_coding.FitGear.coach;

import com.amouri_coding.FitGear.user.Client;
import com.amouri_coding.FitGear.user.UserBaseEntity;
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
@EntityListeners(AuditingEntityListener.class)
public class Coach extends UserBaseEntity {

    private String description;

    private int yearsOfExperience;

    @ManyToMany
    @JoinTable(
            name = "coach_specialties",
            joinColumns = @JoinColumn(name = "coach_id"),
            inverseJoinColumns = @JoinColumn(name = "specialty_id")
    )
    private List<Specialty> specialties;

    @ManyToMany
    @JoinTable(
            name = "coach_certifications",
            joinColumns = @JoinColumn(name = "coach_id"),
            inverseJoinColumns = @JoinColumn(name = "certification_id")
    )
    private List<Certification> certifications;

    private double monthlyRate;

    private boolean isAvailable;

    private String profilePicture;

    private boolean isVerified;

    private double rating;

    @OneToMany
    private List<Client> clients;
}
