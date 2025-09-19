package com.krish.ticket_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id",updatable = false,nullable = false)
    private UUID id;

    @OneToOne( fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id",nullable = false)
    private Booking booking;

    @OneToOne( cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "qr_code_id")
    private QrCode qrCode;

    private LocalDateTime issuedAt;

}
