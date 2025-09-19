package com.krish.ticket_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "seat_rows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatRow {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id",updatable = false,nullable = false)
    private UUID id;

    private String rowLabel; // A, B, C, etc.

    @ManyToOne
    @JoinColumn(name = "seat_layout_id")
    private SeatLayout seatLayout;

    @OneToMany(mappedBy = "seatRow", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();

    // constructors, getters, setters
}
