package com.krish.ticket_booking.service.impl;

import com.krish.ticket_booking.dto.request.ShowRegisterRequestDto;
import com.krish.ticket_booking.dto.response.ShowResponseDto;
import com.krish.ticket_booking.entity.*;
import com.krish.ticket_booking.entity.enums.LayoutType;
import com.krish.ticket_booking.entity.enums.SeatCategory;
import com.krish.ticket_booking.entity.enums.ShowSeatStatus;
import com.krish.ticket_booking.exception.ShowNotFoundException;
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
import static org.mockito.Mockito.*;

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
    void createShow_ShouldCreateShow_Successfully() {
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
    void createShow_ShouldThrowRunTimeException_WhenMovieNotFound() {

        UUID screenId =UUID.randomUUID();
        UUID movieId =UUID.randomUUID();
        ShowRegisterRequestDto request = new ShowRegisterRequestDto(
                10.00, LocalDateTime.now().plusHours(6),movieId
        );
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> showServiceImpl.createShow(request, screenId));

        assertEquals(runtimeException.getMessage(),"Movie not found");

        verify(movieRepository).findById(movieId);
        verify(screenRepository,never()).findById(any());
        verify(showRepository,never()).save(any(Show.class));
        verify(showMapper,never()).toDto(any());
    }

    @Test
    void createShow_ShouldThrowRunTimeException_WhenScreenNotFound() {

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

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(screenRepository.findById(screenId)).thenReturn(Optional.empty());

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> showServiceImpl.createShow(request, screenId));

        assertEquals(runtimeException.getMessage(),"Screen not found");

        verify(movieRepository).findById(movieId);
        verify(screenRepository).findById(screenId);
        verify(showRepository,never()).save(any(Show.class));
        verify(showMapper,never()).toDto(any());
    }

//    @Test
//    void createShow_ShouldThrowRunTimeException_WhenLayoutNotFound() {
//
//        UUID screenId =UUID.randomUUID();
//        UUID movieId =UUID.randomUUID();
//        ShowRegisterRequestDto request = new ShowRegisterRequestDto(
//                10.00, LocalDateTime.now().plusHours(6),movieId
//        );
//
//        Movie movie = Movie.builder()
//                .id(movieId)
//                .duration(120)
//                .active(true)
//                .genre("Action")
//                .title("KalamKaval")
//                .build();
//
//        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
//        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> showServiceImpl.createShow(request, screenId));
//
//        assertEquals(runtimeException.getMessage(),"Seat Layout not found");
//
//        verify(movieRepository).findById(movieId);
//        verify(screenRepository).findById(screenId);
//        verify(showRepository,never()).save(any(Show.class));
//        verify(showMapper,never()).toDto(any());
//    }



    @Test
    void getShow_shouldFetchShowById_Successfully() {

        UUID screenId = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();
        UUID showId = UUID.randomUUID();

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
                .id(showId)
                .movie(movie)
                .screen(screen)
                .startTime(LocalDateTime.now().plusHours(3).plusMinutes(30))
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

        when(showRepository.findById(showId)).thenReturn(Optional.of(savedShow));
        when(showMapper.toDto(savedShow)).thenReturn(showResponseDto);

        ShowResponseDto result = showServiceImpl.getShow(showId);

        assertEquals(result.id(),showResponseDto.id());
        verify(showRepository).findById(showId);
        verify(showMapper).toDto(savedShow);
    }

    @Test
    void getShow_shouldThrowShowNotFoundException_whenShowNotExistById() {

        UUID showId = UUID.randomUUID();

        ShowNotFoundException showNotFoundException = assertThrows(ShowNotFoundException.class, () -> showServiceImpl.getShow(showId));

        assertEquals(showNotFoundException.getMessage(),"Show not found");
        verify(showRepository).findById(showId);
        verify(showMapper,never()).toDto(any());
    }

    @Test
    void updateShow_shouldUpdate_successfully() {
        UUID screenId = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();
        UUID showId = UUID.randomUUID();
        UUID screenIdUpdated = UUID.randomUUID();
        UUID movieIdUpdated = UUID.randomUUID();

        ShowRegisterRequestDto request = new ShowRegisterRequestDto(
                20.00, LocalDateTime.now().plusDays(1).plusHours(3).plusMinutes(30),movieIdUpdated
        );

        Movie movie = Movie.builder()
                .id(movieId)
                .duration(120)
                .active(true)
                .genre("Action")
                .title("Turbo")
                .build();

        Screen screen = Screen.builder()
                .id(screenId)
                .name("Screen1")
                .build();

        Show savedShow = Show.builder()
                .id(showId)
                .movie(movie)
                .screen(screen)
                .startTime(LocalDateTime.now().plusHours(3).plusMinutes(30))
                .extraCharge(10)
                .showSeats(new ArrayList<>())
                .build();


        Movie movieUpdated = Movie.builder()
                .id(movieIdUpdated)
                .duration(120)
                .active(true)
                .genre("Thriller")
                .title("KalamKaval")
                .build();

        Seat seat1Updated = Seat.builder()
                .seatNumber(20)
                .id(UUID.randomUUID())
                .category(SeatCategory.PREMIUM)
                .price(248.00)
                .build();
        Seat seat2Updated = Seat.builder()
                .seatNumber(21)
                .id(UUID.randomUUID())
                .category(SeatCategory.PREMIUM)
                .price(248.00)
                .build();

        SeatRow seatRowUpdated = SeatRow.builder()
                .id(UUID.randomUUID())
                .seats(List.of(seat1Updated,seat2Updated))
                .rowLabel("B1")
                .build();
        seat1Updated.setSeatRow(seatRowUpdated);
        seat2Updated.setSeatRow(seatRowUpdated);

        SeatLayout layoutUpdated = SeatLayout.builder()
                .id(UUID.randomUUID())
                .layoutType(LayoutType.IMAX)
                .rows(List.of(seatRowUpdated))
                .build();
        seatRowUpdated.setSeatLayout(layoutUpdated);

        Screen screenUpdated = Screen.builder()
                .id(screenIdUpdated)
                .name("Screen1Updated")
                .seatLayout(layoutUpdated)
                .build();
        layoutUpdated.setScreen(screenUpdated);

        Show savedShowUpdated = Show.builder()
                .id(showId)
                .movie(movieUpdated)
                .screen(screenUpdated)
                .startTime(request.startTime())
                .extraCharge(20)
                .build();

        ShowResponseDto showResponseDto = new ShowResponseDto(
                savedShowUpdated.getId(),
                savedShowUpdated.getExtraCharge(),
                savedShowUpdated.getStartTime(),
                savedShowUpdated.getMovie().getId(),
                savedShowUpdated.getScreen().getId()
        );

        when(showRepository.findById(showId)).thenReturn(Optional.of(savedShow));
        when(movieRepository.findById(movieIdUpdated)).thenReturn(Optional.of(movieUpdated));
        when(screenRepository.findById(screenIdUpdated)).thenReturn(Optional.of(screenUpdated));
        when(showRepository.save(any(Show.class))).thenReturn(savedShowUpdated);
        when(showMapper.toDto(savedShowUpdated)).thenReturn(showResponseDto);

        ShowResponseDto result = showServiceImpl.updateShow(request, screenIdUpdated, showId);

        assertEquals(movieUpdated.getId(),result.movieId());
        assertEquals(screenUpdated.getId(),result.screenId());
        assertEquals(request.startTime(),result.startTime());
        assertEquals(20,result.extraCharge());
        assertEquals(showId,result.id());

        verify(showRepository).findById(showId);
        verify(showRepository).save(any(Show.class));
        verify(screenRepository).findById(screenIdUpdated);
        verify(movieRepository).findById(movieIdUpdated);
        verify(showMapper).toDto(savedShowUpdated);

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