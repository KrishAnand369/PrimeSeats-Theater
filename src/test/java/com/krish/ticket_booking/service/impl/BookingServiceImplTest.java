package com.krish.ticket_booking.service.impl;

import com.krish.ticket_booking.dto.request.ReserveRequest;
import com.krish.ticket_booking.dto.response.ReserveResponse;
import com.krish.ticket_booking.entity.Seat;
import com.krish.ticket_booking.entity.Show;
import com.krish.ticket_booking.entity.ShowSeat;
import com.krish.ticket_booking.entity.enums.ShowSeatStatus;
import com.krish.ticket_booking.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
}
