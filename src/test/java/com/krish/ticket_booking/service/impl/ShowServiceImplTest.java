package com.krish.ticket_booking.service.impl;

import com.krish.ticket_booking.dto.request.ShowRegisterRequestDto;
import com.krish.ticket_booking.dto.response.ShowLayoutResponseDto;
import com.krish.ticket_booking.dto.response.ShowResponseDto;
import com.krish.ticket_booking.dto.response.ShowRowResponseDto;
import com.krish.ticket_booking.dto.response.ShowSeatResponseDto;
import com.krish.ticket_booking.entity.*;
import com.krish.ticket_booking.entity.enums.LayoutType;
import com.krish.ticket_booking.entity.enums.RoleEnum;
import com.krish.ticket_booking.entity.enums.SeatCategory;
import com.krish.ticket_booking.entity.enums.ShowSeatStatus;
import com.krish.ticket_booking.exception.ShowNotFoundException;
import com.krish.ticket_booking.exception.UserNotFoundException;
import com.krish.ticket_booking.mapper.ShowMapper;
import com.krish.ticket_booking.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

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

    @Mock
    private Authentication authentication;

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
    void getListOfShowsByManager_ShouldFetchShowsByManagerId_successfully() {
        UUID managerId = UUID.randomUUID();
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();
        UUID id4 = UUID.randomUUID();
        UUID screenId1 = UUID.randomUUID();
        UUID screenId2 = UUID.randomUUID();
        UUID screenId3 = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();
        String email = "user@manager.com";
        LocalDateTime now = LocalDateTime.now();

        Movie movie = Movie.builder()
                .id(movieId)
                .title("KalamKaval")
                .language("Malayalam")
                .build();

        User user = User.builder()
                .role(RoleEnum.MANAGER)
                .email(email)
                .name("manager")
                .id(managerId)
                .build();

        Theater theater1 = Theater.builder()
                .manager(user)
                .name("Jose Cinemas")
                .build();

        Theater theater2 = Theater.builder()
                .manager(user)
                .name("Maruthi Cinemas")
                .build();

        Screen screen1 = Screen.builder()
                .id(screenId1)
                .theater(theater1)
                .name("screen1")
                .build();
        theater1.setScreens(List.of(screen1));

        Screen screen2 = Screen.builder()
                .id(screenId2)
                .theater(theater2)
                .name("screen2")
                .build();

        Screen screen3 = Screen.builder()
                .id(screenId3)
                .theater(theater2)
                .name("screen3")
                .build();


        theater2.setScreens(List.of(screen2,screen3));

        Show show1 = Show.builder()
                .id(id1)
                .movie(movie)
                .extraCharge(10)
                .screen(screen1)
                .startTime(now.plusHours(1))
                .build();

        Show show2 = Show.builder()
                .screen(screen1)
                .id(id2)
                .movie(movie)
                .extraCharge(10)
                .startTime(now.plusHours(2))
                .build();
        screen1.setShows(List.of(show1,show2));

        Show show3 = Show.builder()
                .id(id3)
                .screen(screen2)
                .movie(movie)
                .extraCharge(10)
                .startTime(now.plusHours(3))
                .build();
        screen2.setShows(List.of(show3));

        Show show4 = Show.builder()
                .id(id4)
                .movie(movie)
                .extraCharge(10)
                .screen(screen3)
                .startTime(now.plusHours(4))
                .build();
        screen3.setShows(List.of(show4));

        ShowResponseDto show1Dto = new ShowResponseDto(
                id1,
                10,
                now.plusHours(1),
                movieId,
                screenId1
        );

        ShowResponseDto show2Dto = new ShowResponseDto(
                id2,
                10,
                now.plusHours(2),
                movieId,
                screenId1
        );

        ShowResponseDto show3Dto = new ShowResponseDto(
                id3,
                10,
                now.plusHours(3),
                movieId,
                screenId2
        );

        ShowResponseDto show4Dto = new ShowResponseDto(
                id4,
                10,
                now.plusHours(4),
                movieId,
                screenId3
        );

        when(authentication.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(theaterRepository.findByManagerId(managerId)).thenReturn(List.of(theater1,theater2));
        when(showMapper.toDto(show1)).thenReturn(show1Dto);
        when(showMapper.toDto(show2)).thenReturn(show2Dto);
        when(showMapper.toDto(show3)).thenReturn(show3Dto);
        when(showMapper.toDto(show4)).thenReturn(show4Dto);

        List<ShowResponseDto> listOfShowsByManager = showServiceImpl.getListOfShowsByManager(authentication);

        assertEquals(4,listOfShowsByManager.size());
        assertTrue(listOfShowsByManager.contains(show1Dto));
        assertTrue(listOfShowsByManager.contains(show2Dto));
        assertTrue(listOfShowsByManager.contains(show3Dto));
        assertTrue(listOfShowsByManager.contains(show4Dto));

        verify(authentication).getName();
        verify(userRepository).findByEmail(email);
        verify(theaterRepository).findByManagerId(managerId);
        verify(showMapper).toDto(show1);
        verify(showMapper).toDto(show2);
        verify(showMapper).toDto(show4);
        verify(showMapper).toDto(show3);

    }

    @Test
    void getListOfShowsByManager_ShouldThrowManagerNotFundException_whenUserIsNotFound() {
        String email ="user@example.com";

        when(authentication.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () -> showServiceImpl.getListOfShowsByManager(authentication));

        assertEquals("Manager Not Found Exception",userNotFoundException.getMessage());

        verify(authentication).getName();
        verify(userRepository).findByEmail(email);
        verify(theaterRepository,never()).findByManagerId(any());
        verify(showMapper,never()).toDto(any());
    }

    @Test
    void getShowLayout_ShouldGetShowLayoutByShowId_Successfully() {
        UUID showId = UUID.randomUUID();
        UUID seatRowId = UUID.randomUUID();
        LocalDateTime showStartTime = LocalDateTime.now();

        Movie movie = Movie.builder()
                .id(UUID.randomUUID())
                .language("Malayalam")
                .title("KalamKaval")
                .build();

        Show show = Show.builder()
                .id(showId)
                .extraCharge(10)
                .movie(movie)
                .startTime(showStartTime)
                .build();

        SeatRow seatRow = SeatRow.builder()
                .rowLabel("A")
                .id(seatRowId)
                .build();

        Seat seat1 = Seat.builder()
                .price(150.00)
                .seatNumber(10)
                .seatRow(seatRow)
                .build();

        Seat seat2 = Seat.builder()
                .price(150.00)
                .seatNumber(11)
                .seatRow(seatRow)
                .build();

        seatRow.setSeats(List.of(seat1,seat2));



        ShowSeat showSeat1 = ShowSeat.builder()
                .id(UUID.randomUUID())
                .show(show)
                .seat(seat1)
                .status(ShowSeatStatus.AVAILABLE)
                .build();

        ShowSeat showSeat2 = ShowSeat.builder()
                .id(UUID.randomUUID())
                .show(show)
                .seat(seat2)
                .status(ShowSeatStatus.AVAILABLE)
                .build();

        show.setShowSeats(List.of(showSeat1,showSeat2));

        

        when(showRepository.findById(showId)).thenReturn(Optional.of(show));

        ShowLayoutResponseDto result = showServiceImpl.getShowLayout(showId);

        assertNotNull(result);
        assertEquals(showId, result.showId());
        assertEquals("KalamKaval", result.movieTitle());
        assertEquals(showStartTime, result.startTime());

        assertEquals(1, result.rows().size()); // Should have 1 row ("A")
        ShowRowResponseDto rowDto = result.rows().getFirst();
        assertEquals("A", rowDto.rowLabel());

        // Verify Seats in Row
        assertEquals(2, rowDto.seats().size());

        // Verify Seat 1 Details (Price should be Base + Extra = 150 + 10 = 160)
        List<ShowSeatResponseDto> seats = rowDto.seats();
        assertTrue( seats.stream().anyMatch(s1->
            s1.status().equals(ShowSeatStatus.AVAILABLE)
        ));
        assertTrue(seats.stream().anyMatch(s1->
                s1.seatLabel().equals("A10")));
        assertTrue(seats.stream().anyMatch(s1->
                s1.price().equals(160.00)));

        verify(showRepository).findById(showId);



    }

    @Test
    void getShowBookedSeats_ShouldFetchBookedSeats_Successfully() {
        UUID showId = UUID.randomUUID();
        UUID showSeat1Id = UUID.randomUUID();
        UUID showSeat2Id = UUID.randomUUID();
        UUID showSeat3Id = UUID.randomUUID();

        Show show = Show.builder()
                .id(showId)
                .extraCharge(10)
                .build();

        ShowSeat showSeat1 = ShowSeat.builder()
                .id(showSeat1Id)
                .show(show)
                .status(ShowSeatStatus.BOOKED)
                .build();

        ShowSeat showSeat2 = ShowSeat.builder()
                .id(showSeat2Id)
                .show(show)
                .status(ShowSeatStatus.AVAILABLE)
                .build();

        ShowSeat showSeat3 = ShowSeat.builder()
                .id(showSeat3Id)
                .show(show)
                .status(ShowSeatStatus.BOOKED)
                .build();

        show.setShowSeats(List.of(showSeat1,showSeat2,showSeat3));

        when(showRepository.findById(showId)).thenReturn(Optional.of(show));
        when(showSeatRepository.findByShowIdAndStatus(showId,ShowSeatStatus.BOOKED)).thenReturn(List.of(showSeat1,showSeat3));

        List<UUID> result = showServiceImpl.getShowBookedSeats(showId);

        assertNotNull(result);
        assertEquals(2,result.size());
        assertTrue(result.contains(showSeat1Id));
        assertFalse(result.contains(showSeat2Id));
        assertTrue(result.contains(showSeat3Id));

        verify(showRepository).findById(showId);
        verify(showSeatRepository).findByShowIdAndStatus(showId,ShowSeatStatus.BOOKED);

    }

    @Test
    void listShowsByScreenId() {
    }
}