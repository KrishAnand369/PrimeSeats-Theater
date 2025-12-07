package com.krish.ticket_booking.service.impl;

import com.krish.ticket_booking.dto.response.ReserveResponse;
import com.krish.ticket_booking.dto.response.TicketResponse;
import com.krish.ticket_booking.dto.request.BookingRequestDto;
import com.krish.ticket_booking.dto.request.ReserveRequest;
import com.krish.ticket_booking.dto.response.BookingResponseDto;
import com.krish.ticket_booking.entity.*;
import com.krish.ticket_booking.entity.enums.BookingStatusEnum;
import com.krish.ticket_booking.entity.enums.ShowSeatStatus;
import com.krish.ticket_booking.repository.*;
import com.krish.ticket_booking.service.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final int MAX_SEATS_PER_BOOKING = 10;
    private static final int RESERVATION_MINUTES = 15;

    private final ShowSeatRepository showSeatRepo;
    private final ShowRepository showRepo;
    private final BookingRepository bookingRepo;
    private final BookingSeatRepository bookingSeatRepo;
    private final TicketRepository ticketRepo;
    private final UserRepository userRepo;

        @Override
        @Transactional
        public BookingResponseDto bookTickets(BookingRequestDto request) {
        return null;
    }

    @Transactional
    @Override
    public ReserveResponse reserveSeats(ReserveRequest request, Optional<UUID> currentUserId) {
        // 1) Basic validations
        if (request.seatIds().size() > MAX_SEATS_PER_BOOKING) {
            throw new IllegalArgumentException("You can book at most " + MAX_SEATS_PER_BOOKING + " seats");
        }

        // 2) Load Show (for price add-on / validation)
        Show show = showRepo.findById(request.showId())
                .orElseThrow(() -> new NoSuchElementException("Show not found"));

        // 3) Lock seats deterministically to avoid deadlocks
        List<UUID> sortedIds = request.seatIds().stream().sorted().toList();
        List<ShowSeat> seats = showSeatRepo.findAllByIdForUpdate(sortedIds);

        if (seats.size() != sortedIds.size()) {
            throw new IllegalArgumentException("One or more seats not found");
        }

        // 4) Validate all seats belong to the same show and are AVAILABLE
        for (ShowSeat s : seats) {
            if (!s.getShow().getId().equals(request.showId())) {
                throw new IllegalArgumentException("Seat does not belong to the selected show: " + s.getId());
            }
            if (s.getStatus() != ShowSeatStatus.AVAILABLE) {
                throw new IllegalStateException("Seat not available: " + s.getId());
            }
        }

        // 5) Resolve user (guest or registered)
        User user = null;
        if (currentUserId.isPresent()) {
            user = userRepo.findById(currentUserId.get())
                    .orElseThrow(() -> new NoSuchElementException("User not found"));
        }

        // 6) Create booking in PENDING_PAYMENT
        Booking booking = new Booking();
        booking.setStatus(BookingStatusEnum.PENDING_PAYMENT);
        booking.setShow(show);
        booking.setUser(user);
        if (user == null) {
            booking.setGuestEmail(request.guestEmail());
        }
        booking.setCreatedAt(java.time.LocalDateTime.now());

        // 7) Compute total at time of reservation (snapshot)
        //    Here we don't have price on ShowSeat; use Seat.price + Show.price (as extra/markup).
        double showExtra = show.getExtraCharge(); // double primitive → default 0.0 if not used
        double total = seats.stream()
                .mapToDouble(ss -> (ss.getSeat().getPrice() != null ? ss.getSeat().getPrice() : 0.0) + showExtra)
                .sum();
        booking.setTotalAmount(total);

        booking = bookingRepo.save(booking);

        // 8) Associate seats to booking and mark as RESERVED with TTL
        Instant until = Instant.now().plus(RESERVATION_MINUTES, ChronoUnit.MINUTES);
        List<BookingSeat> bookingSeats = new ArrayList<>();

        for (ShowSeat s : seats) {
            s.setStatus(ShowSeatStatus.RESERVED);
            s.setReservedUntil(until);
            s.setBooking(booking);

            BookingSeat bs = BookingSeat.builder()
                    .booking(booking)
                    .seat(s)
                    .build();
            bookingSeats.add(bs);
        }

        bookingSeatRepo.saveAll(bookingSeats);

        // 9) Response
        return new ReserveResponse(
                booking.getId(),
                booking.getStatus(),
                until,
                seats.stream().map(ShowSeat::getId).toList(),
                total
        );
    }

    @Transactional
    @Override
    public TicketResponse confirmBooking(UUID bookingId) {
        // 1) Load booking + seats
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Booking not found"));

        if (booking.getStatus() != BookingStatusEnum.PENDING_PAYMENT) {
            throw new IllegalStateException("Booking is not pending payment");
        }

        List<ShowSeat> seats = booking.getBookingSeats().stream()
                .map(BookingSeat::getSeat)
                .toList();

        if (seats.isEmpty()) {
            throw new IllegalStateException("No seats attached to booking");
        }

        // 2) Validate reservation ownership & TTL
        Instant now = Instant.now();
        for (ShowSeat s : seats) {
            if (s.getStatus() != ShowSeatStatus.RESERVED) {
                throw new IllegalStateException("Seat not reserved: " + s.getId());
            }
            if (s.getReservedUntil() == null || s.getReservedUntil().isBefore(now)) {
                throw new IllegalStateException("Reservation expired for seat: " + s.getId());
            }
            if (s.getBooking() == null || !s.getBooking().getId().equals(bookingId)) {
                throw new IllegalStateException("Seat reserved by another booking: " + s.getId());
            }
        }

        // 3) Mark seats BOOKED, clear reservation fields
        for (ShowSeat s : seats) {
            s.setStatus(ShowSeatStatus.BOOKED);
            s.setReservedUntil(null);
            // keep s.setBooking(booking) to preserve linkage (model keeps booking on ShowSeat)
        }

        // 4) Mark booking confirmed & create ticket
        booking.setStatus(BookingStatusEnum.CONFIRMED);

        Ticket ticket = Ticket.builder()
                .booking(booking)
                 // generate real QR later
                .issuedAt(java.time.LocalDateTime.now())
                .build();

        ticket = ticketRepo.save(ticket);
        booking.setTicket(ticket);

        // 5) Build response
        List<String> labels = seats.stream()
                .map(ss -> {
                    String row = ss.getSeat().getSeatRow().getRowLabel();
                    return row + ss.getSeat().getSeatNumber();
                })
                .collect(Collectors.toList());

        return new TicketResponse(
                ticket.getId(),
                booking.getId(),
                booking.getShow().getId(),
                labels,
                booking.getTotalAmount(),
                ticket.getQrCode()
        );
    }

    @Override
    @Transactional
    public int releaseExpiredReservations() {
        return showSeatRepo.releaseExpired(Instant.now());
    }
}
