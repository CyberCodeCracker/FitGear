package com.amouri_coding.FitGear.diet;

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
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    private String title;

    private String content;

    @ManyToOne
    @JoinColumn(name = "PROGRAM_ID")
    private DietProgram program;
}
