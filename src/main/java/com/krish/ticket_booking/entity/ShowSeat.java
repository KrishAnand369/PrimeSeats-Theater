package com.krish.ticket_booking.entity;

import com.krish.ticket_booking.entity.enums.ShowSeatStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "show_seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id",updatable = false,nullable = false)
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ShowSeatStatus status = ShowSeatStatus.AVAILABLE; // AVAILABLE, BOOKED, RESERVED, BLOCKED

    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;  // Reference to physical seat

    @ManyToOne
    @JoinColumn(name = "show_id")
    private Show show;  // Reference to specific showtime

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;  // If booked, reference to booking

    private Instant reservedUntil; // set to now + 15 minutes

    @Version
    private Long version; // optimistic lock safeguard
}


