package com.vagabonder.entity;

import com.vagabonder.enums.ItineraryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Itinerary {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    @Column(columnDefinition = "TEXT") //for long texts (default varchar is 255
    private String activities;
    @ManyToOne
    private User user;
    private LocalDate fromDate;
    private LocalDate toDate;
    @Enumerated(EnumType.STRING)
    private ItineraryStatus status;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
