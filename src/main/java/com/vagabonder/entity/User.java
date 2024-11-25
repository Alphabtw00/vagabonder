package com.vagabonder.entity;


import com.vagabonder.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails { //todo add image attribute
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String email;
    private String password;
    private String fullName;
    @Column(length = 500)
    private String profilePhotoPath;
    @Column(length = 500)
    private String coverPhotoPath;
    private Integer age;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @LastModifiedDate
    private LocalDate dateOfBirth;
    private String residence;
    private String languages;
    private String ethnicity;
    private String religion;
    private String occupation;
    private String placesTravelled; //todo when adding new itinerary, attach its name to substring as this
    private String bio;
    private String bestTravelStory;
    @ElementCollection
    @Builder.Default
    private List<String> travelMemoryPaths = new ArrayList<>();
    @CreationTimestamp
    private LocalDateTime registerDate;
    @ElementCollection
    @Builder.Default //builder doesnt initialize, so after persisting its still null
    private Set<UUID> ignoredUserIds = new HashSet<>();
    @PrePersist
    public void setAge(){
        this.age=Period.between(this.dateOfBirth, LocalDate.now()).getYears();
    }
    @PostPersist
    public void ignoreOwnId(){
        this.ignoredUserIds.add(this.id);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return email;
    }
}

