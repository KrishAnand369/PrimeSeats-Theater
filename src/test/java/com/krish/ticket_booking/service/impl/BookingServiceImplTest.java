package com.krish.ticket_booking.service.impl;

import com.krish.ticket_booking.dto.request.ReserveRequest;
import com.krish.ticket_booking.dto.response.ReserveResponse;
import com.krish.ticket_booking.dto.response.TicketResponse;
import com.krish.ticket_booking.entity.*;
import com.krish.ticket_booking.entity.enums.BookingStatusEnum;
import com.krish.ticket_booking.entity.enums.ShowSeatStatus;
import com.krish.ticket_booking.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private ShowSeatRepository showSeatRepo;
    @Mock
    private ShowRepository showRepo;
    @Mock
    private BookingRepository bookingRepo;
    @Mock
    private BookingSeatRepository bookingSeatRepo;
    @Mock
    private TicketRepository ticketRepo;
    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void reserveSeatsShouldReserveSeatsSuccessfully() {
        // Arrange
        UUID showId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();
        List<UUID> seatIds = List.of(seatId);
        String guestEmail = "example@example.com";

        ReserveRequest reserveRequest = new ReserveRequest(
                showId,
                seatIds,
                guestEmail
        );

        Show show = new Show();
        show.setId(showId);
        show.setExtraCharge(10.0);

        Seat seat = new Seat();
        seat.setPrice(100.0);

        ShowSeat showSeat = new ShowSeat();
        showSeat.setId(seatId);
        showSeat.setShow(show);
        showSeat.setSeat(seat);
        showSeat.setStatus(ShowSeatStatus.AVAILABLE);

        when(showRepo.findById(showId)).thenReturn(Optional.of(show));
        when(showSeatRepo.findAllByIdForUpdate(any())).thenReturn(List.of(showSeat));
        when(bookingRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ReserveResponse response = bookingService.reserveSeats(reserveRequest, Optional.empty());

        // Assert
        assertNotNull(response);
        assertEquals(110.0, response.totalAmount());
        verify(bookingRepo).save(any());
        verify(bookingSeatRepo).saveAll(any());
    }

    @Test
    void reserveSeats_ShouldThrowException_WhenSeatIsAlreadyBooked(){
        // Arrange
        UUID showId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();
        List<UUID> seatIds = List.of(seatId);
        String guestEmail = "example@example.com";

        ReserveRequest reserveRequest = new ReserveRequest(
                showId,
                seatIds,
                guestEmail
        );

        Show show = new Show();
        show.setId(showId);
        show.setExtraCharge(10.0);

        Seat seat = new Seat();
        seat.setPrice(100.0);

        ShowSeat showSeat = new ShowSeat();
        showSeat.setId(seatId);
        showSeat.setShow(show);
        showSeat.setSeat(seat);
        showSeat.setStatus(ShowSeatStatus.BOOKED);

        when(showRepo.findById(showId)).thenReturn(Optional.of(show));
        when(showSeatRepo.findAllByIdForUpdate(any())).thenReturn(List.of(showSeat));

        // Act
        IllegalStateException illegalAccessException = assertThrows(IllegalStateException.class, () -> bookingService.reserveSeats(reserveRequest, Optional.empty()));

        // Assert
        assertEquals("Seat not available: "+seatId, illegalAccessException.getMessage());
        verify(bookingRepo,never()).save(any());
        verify(bookingSeatRepo,never()).saveAll(any());
    }

    @Test
    void reserveSeats_ShouldThrowException_WhenNumberOfSeatsExceedsLimit(){
        // Arrange
        UUID showId = UUID.randomUUID();
        List<UUID> seatIds = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
           seatIds.add(UUID.randomUUID());
        }
        String guestEmail = "example@example.com";

        ReserveRequest reserveRequest = new ReserveRequest(
                showId,
                seatIds,
                guestEmail
        );

        // Act
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> bookingService.reserveSeats(reserveRequest, Optional.empty()));

        // Assert
        assertEquals("You can book at most 10 seats", illegalArgumentException.getMessage());
        verify(bookingRepo,never()).save(any());
        verify(bookingSeatRepo,never()).saveAll(any());
    }

    @Test
    void reserveSeats_ShouldLinkToUser_WhenUserIsRegistered(){
        // Arrange
        UUID showId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        List<UUID> seatIds = List.of(seatId);
        String guestEmail = "user@example.com";

        User user = User.builder()
                .id(userId)
                .name("Krish")
                .build();

        ReserveRequest reserveRequest = new ReserveRequest(
                showId,
                seatIds,
                guestEmail
        );

        Show show = new Show();
        show.setId(showId);
        show.setExtraCharge(10.0);

        Seat seat = new Seat();
        seat.setPrice(100.0);

        ShowSeat showSeat = new ShowSeat();
        showSeat.setId(seatId);
        showSeat.setShow(show);
        showSeat.setSeat(seat);
        showSeat.setStatus(ShowSeatStatus.AVAILABLE);

        when(showRepo.findById(showId)).thenReturn(Optional.of(show));
        when(showSeatRepo.findAllByIdForUpdate(any())).thenReturn(List.of(showSeat));
        when(bookingRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        // Act
        ReserveResponse response = bookingService.reserveSeats(reserveRequest, Optional.of(userId));

        // Assert
        assertNotNull(response);
        assertEquals(110.0, response.totalAmount());
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class); //same as using @Captor but as this is used only in this methode this is better
        verify(bookingRepo).save(bookingCaptor.capture());
        Booking savedBooking = bookingCaptor.getValue();
        assertEquals(user, savedBooking.getUser());
        verify(bookingSeatRepo).saveAll(any());
    }

    @Test
    void reserveSeats_ShouldThrowException_WhenSeatBelongsToDifferentShow(){
        // Arrange
        UUID showId = UUID.randomUUID();
        UUID showId1 = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();
        List<UUID> seatIds = List.of(seatId);
        String guestEmail = "example@example.com";

        ReserveRequest reserveRequest = new ReserveRequest(
                showId,
                seatIds,
                guestEmail
        );

        Show show = new Show();
        show.setId(showId);
        show.setExtraCharge(10.0);

        Show show1 = Show.builder()
                .id(showId1)
                .extraCharge(15.0)
                .build();

        Seat seat = new Seat();
        seat.setPrice(100.0);

        ShowSeat showSeat = new ShowSeat();
        showSeat.setId(seatId);
        showSeat.setShow(show1);
        showSeat.setSeat(seat);
        showSeat.setStatus(ShowSeatStatus.AVAILABLE);

        when(showRepo.findById(showId)).thenReturn(Optional.of(show));
        when(showSeatRepo.findAllByIdForUpdate(any())).thenReturn(List.of(showSeat));
       // when(bookingRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        IllegalArgumentException response = assertThrows(IllegalArgumentException.class, () -> bookingService.reserveSeats(reserveRequest, Optional.empty()));

        // Assert
        assertEquals("Seat does not belong to the selected show: " + seatId, response.getMessage());
        verify(bookingRepo,never()).save(any());
        verify(bookingSeatRepo,never()).saveAll(any());
    }

    @Test
    void  confirmBooking_shouldConfirmBooking_Successfully(){
        UUID bookingId = UUID.randomUUID();
        UUID showId1 = UUID.randomUUID();
        UUID seatId1 = UUID.randomUUID();
        UUID seatId2 = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();

        Booking booking = Booking.builder()
                .id(bookingId)
                .status(BookingStatusEnum.PENDING_PAYMENT)
                .build();

        Show show1 = Show.builder()
                .id(showId1)
                .extraCharge(15.0)
                .build();

        SeatRow seatRow = SeatRow.builder()
                .rowLabel("A")
                .build();


        booking.setShow(show1);
        booking.setTotalAmount(250);
        Seat seat = new Seat();
        seat.setSeatRow(seatRow);
        seat.setSeatNumber(11);
        seat.setPrice(100.0);

        Seat seat1 = new Seat();
        seat1.setPrice(100.0);
        seat1.setSeatRow(seatRow);
        seat1.setSeatNumber(12);

        ShowSeat showSeat = new ShowSeat();
        showSeat.setId(seatId1);
        showSeat.setShow(show1);
        showSeat.setSeat(seat);
        showSeat.setBooking(booking);
        showSeat.setReservedUntil(Instant.now().plusMillis(1000000));
        showSeat.setStatus(ShowSeatStatus.RESERVED);

        ShowSeat showSeat1 = new ShowSeat();
        showSeat1.setId(seatId2);
        showSeat1.setShow(show1);
        showSeat1.setSeat(seat1);
        showSeat1.setBooking(booking);
        showSeat1.setReservedUntil(Instant.now().plusMillis(1000000));
        showSeat1.setStatus(ShowSeatStatus.RESERVED);

        BookingSeat bookingSeat1 = BookingSeat.builder()
                .booking(booking)
                .seat(showSeat)
                .build();

        BookingSeat bookingSeat2 = BookingSeat.builder()
                .booking(booking)
                .seat(showSeat1)
                .build();

        booking.setBookingSeats(List.of(bookingSeat2,bookingSeat1));

        Ticket savedTicket = Ticket.builder()
                .booking(booking)
                .issuedAt(LocalDateTime.now())
                .id(ticketId)
                .build();

        when(ticketRepo.save(any())).thenReturn(savedTicket);
        when(bookingRepo.findById(bookingId)).thenReturn(Optional.of(booking));

        TicketResponse ticketResponse = bookingService.confirmBooking(bookingId);

        assertEquals(ticketId,ticketResponse.ticketId());
        assertEquals(bookingId,ticketResponse.bookingId());
        assertEquals(showId1,ticketResponse.showId());

        // Verify Booking Status Updated
        assertEquals(BookingStatusEnum.CONFIRMED, booking.getStatus());
        // Verify Seat Statuses Updated
        assertEquals(ShowSeatStatus.BOOKED, showSeat.getStatus());
        assertEquals(ShowSeatStatus.BOOKED, showSeat1.getStatus());

        verify(ticketRepo).save(any());
        verify(bookingRepo).findById(bookingId);

    }

}
