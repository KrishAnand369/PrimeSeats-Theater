package com.krish.ticket_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "movies")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id",updatable = false,nullable = false)
    private UUID id;

    private String title;

    private String genre;

    private String language;

    private String posterUrl;

    private int duration;

    private boolean active;

    private java.time.LocalDateTime createdAt;
}

