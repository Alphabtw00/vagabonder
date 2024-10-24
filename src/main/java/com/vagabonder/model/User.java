package com.vagabonder.model;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String fullName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String residence;
    @Transient
    private List<String> languages;

}

enum Gender {
    MALE,
    FEMALE,
    OTHER
}
