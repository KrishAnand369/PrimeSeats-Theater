package com.krish.ticket_booking.service;

import com.krish.ticket_booking.dto.request.BookingRequestDto;
import com.krish.ticket_booking.dto.request.ReserveRequest;
import com.krish.ticket_booking.dto.response.BookingResponseDto;
import com.krish.ticket_booking.dto.response.ReserveResponse;
import com.krish.ticket_booking.dto.response.TicketResponse;

import java.util.Optional;
import java.util.UUID;

public interface BookingService {
    BookingResponseDto bookTickets(BookingRequestDto request);
    ReserveResponse reserveSeats(ReserveRequest request, Optional<UUID> currentUserId);
    TicketResponse confirmBooking(UUID bookingId);
    int releaseExpiredReservations();
}
