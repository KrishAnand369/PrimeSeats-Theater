package com.krish.ticket_booking.entity;

import com.krish.ticket_booking.entity.enums.BookingStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id",updatable = false,nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private BookingStatusEnum status;

    private double totalAmount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String guestEmail;

    @ManyToOne
    @JoinColumn(name = "show_id")
    private Show show;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<BookingSeat> bookingSeats;
}

