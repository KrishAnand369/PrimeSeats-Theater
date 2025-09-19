package com.krish.ticket_booking.controller;

import com.krish.ticket_booking.dto.request.ReserveRequest;
import com.krish.ticket_booking.dto.response.ReserveResponse;
import com.krish.ticket_booking.dto.response.TicketResponse;
import com.krish.ticket_booking.entity.User;
import com.krish.ticket_booking.repository.UserRepository;
import com.krish.ticket_booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    @PostMapping("/reserve")
    public ResponseEntity<ReserveResponse> reserve(@Valid @RequestBody ReserveRequest request,
            Authentication auth) {
        ReserveResponse resp = bookingService.reserveSeats(request, resolveUserId(auth));
        return ResponseEntity.status(201).body(resp);
    }

    private Optional<UUID> resolveUserId(Authentication auth) {
        if (auth != null && auth.isAuthenticated() && auth.getName() != null) {
            return userRepository.findByEmail(auth.getName()).map(User::getId);
        }
        return Optional.empty();
    }


    @PostMapping("/{bookingId}/confirm")
    public ResponseEntity<TicketResponse> confirm(@PathVariable UUID bookingId) {
        TicketResponse ticket = bookingService.confirmBooking(bookingId);
        return ResponseEntity.ok(ticket);
    }
}


