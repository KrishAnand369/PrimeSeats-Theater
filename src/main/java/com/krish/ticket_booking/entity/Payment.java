package com.krish.ticket_booking.entity;

import com.krish.ticket_booking.entity.enums.PaymentStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id",updatable = false,nullable = false)
    private UUID id;

    private String transactionId;

    @Enumerated(EnumType.STRING)
    private PaymentStatusEnum status;

    private double amount;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private java.time.LocalDateTime paymentTime;
}

