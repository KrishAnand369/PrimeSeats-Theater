package com.krish.ticket_booking.entity;

import com.krish.ticket_booking.entity.enums.SeatCategory;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "seats")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id",updatable = false,nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private SeatCategory category;

    private int seatNumber;

    private Double price;

    @ManyToOne
    @JoinColumn(name = "seat_row_id")
    private SeatRow seatRow; // Theater Manager

    private java.time.LocalDateTime createdAt;

    @Transient
    public String getSeatLabel() {
        return seatRow.getRowLabel() + seatNumber;
    }
}

