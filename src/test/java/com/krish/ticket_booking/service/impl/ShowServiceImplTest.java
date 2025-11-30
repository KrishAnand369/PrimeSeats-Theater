package com.krish.ticket_booking.service.impl;

import com.krish.ticket_booking.dto.request.ShowRegisterRequestDto;
import com.krish.ticket_booking.dto.response.ShowResponseDto;
import com.krish.ticket_booking.entity.*;
import com.krish.ticket_booking.entity.enums.LayoutType;
import com.krish.ticket_booking.entity.enums.SeatCategory;
import com.krish.ticket_booking.entity.enums.ShowSeatStatus;
import com.krish.ticket_booking.mapper.ShowMapper;
import com.krish.ticket_booking.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShowServiceImplTest {

    @Mock
    private ShowRepository showRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ScreenRepository screenRepository;

    @Mock
    private TheaterRepository theaterRepository;

    @Mock
    private ShowSeatRepository showSeatRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShowMapper showMapper;

    @InjectMocks
    private ShowServiceImpl showServiceImpl;

    @Test
    void createShow() {
        UUID screenId =UUID.randomUUID();
        UUID movieId =UUID.randomUUID();
        ShowRegisterRequestDto request = new ShowRegisterRequestDto(
                10.00, LocalDateTime.now().plusHours(6),movieId
        );

        Movie movie = Movie.builder()
                .id(movieId)
                .duration(120)
                .active(true)
                .genre("Action")
                .title("KalamKaval")
                .build();

        Seat seat1 = Seat.builder()
                .seatNumber(10)
                .id(UUID.randomUUID())
                .category(SeatCategory.PREMIUM)
                .price(148.00)
                .build();

        Seat seat2 = Seat.builder()
                .seatNumber(11)
                .id(UUID.randomUUID())
                .category(SeatCategory.PREMIUM)
                .price(148.00)
                .build();

        SeatRow seatRow = SeatRow.builder()
                .id(UUID.randomUUID())
                .seats(List.of(seat1,seat2))
                .rowLabel("A1")
                .build();
        seat1.setSeatRow(seatRow);
        seat2.setSeatRow(seatRow);


        SeatLayout layout = SeatLayout.builder()
                .id(UUID.randomUUID())
                .layoutType(LayoutType.SCREEN_X)
                .rows(List.of(seatRow))
                .build();
        seatRow.setSeatLayout(layout);

        Screen screen = Screen.builder()
                .id(screenId)
                .name("Screen1")
                .seatLayout(layout)
                .build();
        layout.setScreen(screen);

        Show savedShow = Show.builder()
                .id(UUID.randomUUID())
                .movie(movie)
                .screen(screen)
                .startTime(request.startTime())
                .extraCharge(10)
                .build();

        ShowSeat showSeat1 = ShowSeat.builder()
                .id(UUID.randomUUID())
                .show(savedShow)
                .seat(seat1)
                .status(ShowSeatStatus.AVAILABLE)
                .build();

        ShowSeat showSeat2 = ShowSeat.builder()
                .id(UUID.randomUUID())
                .show(savedShow)
                .seat(seat2)
                .status(ShowSeatStatus.AVAILABLE)
                .build();
        savedShow.setShowSeats(List.of(showSeat1,showSeat2));

        ShowResponseDto showResponseDto = new ShowResponseDto(
                savedShow.getId(),
                savedShow.getExtraCharge(),
                savedShow.getStartTime(),
                savedShow.getMovie().getId(),
                savedShow.getScreen().getId()
        );

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(screenRepository.findById(screenId)).thenReturn(Optional.of(screen));
        when(showRepository.save(any(Show.class))).thenReturn(savedShow);
        when(showMapper.toDto(savedShow)).thenReturn(showResponseDto);

        ShowResponseDto result = showServiceImpl.createShow(request, screenId);

        assertNotNull(result);
        assertEquals(showResponseDto.id(), result.id());
        assertEquals(showResponseDto.extraCharge(), result.extraCharge());

        verify(movieRepository).findById(movieId);
        verify(screenRepository).findById(screenId);
        verify(showRepository).save(any(Show.class));
        verify(showMapper).toDto(savedShow);
    }

    @Test
    void getShow() {
    }

    @Test
    void updateShow() {
    }

    @Test
    void getListOfShowsByManager() {
    }

    @Test
    void getShowLayout() {
    }

    @Test
    void getShowBookedSeats() {
    }

    @Test
    void listShowsByScreenId() {
    }
}