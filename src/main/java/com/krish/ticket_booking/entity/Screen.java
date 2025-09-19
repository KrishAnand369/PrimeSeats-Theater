package com.krish.ticket_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "screens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id",updatable = false,nullable = false)
    private UUID id;

    private String name;

    private int totalSeats;

    @ManyToOne
    @JoinColumn(name = "theater_id")
    private Theater theater; // Theater Manager

    @OneToOne(mappedBy = "screen", cascade = CascadeType.ALL)
    private SeatLayout seatLayout;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL)
    private List<Show> shows;

    private java.time.LocalDateTime createdAt;
}

