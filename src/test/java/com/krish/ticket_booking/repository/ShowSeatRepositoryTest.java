package com.krish.ticket_booking.repository;

import com.krish.ticket_booking.entity.*;
import com.krish.ticket_booking.entity.enums.LayoutType;
import com.krish.ticket_booking.entity.enums.SeatCategory;
import com.krish.ticket_booking.entity.enums.ShowSeatStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ShowSeatRepositoryTest {

    @Autowired
    ShowSeatRepository showSeatRepository;

    @Autowired
    TestEntityManager testEntityManager;


    @Test
    public void findByShowIdAndStatus_shouldFetchShowSeatSuccessfully(){
        Theater theater = Theater.builder()
                .name("jose")
                .location("Mananthavady")
                .build();
        theater = testEntityManager.persistAndFlush(theater);

        Screen screen = Screen.builder()
                .name("screen 1")
                .theater(theater)
                .totalSeats(124)
                .build();
        screen = testEntityManager.persistAndFlush(screen);

        SeatLayout seatLayout = SeatLayout.builder()
                .layoutType(LayoutType.SCREEN_X)
                .screen(screen)
                .build();
        seatLayout = testEntityManager.persistAndFlush(seatLayout);

        SeatRow seatRow = SeatRow.builder()
                .rowLabel("A")
                .seatLayout(seatLayout)
                .build();
        seatRow = testEntityManager.persistAndFlush(seatRow);

        Seat seat = Seat.builder()
                .seatNumber(10)
                .price(100.00)
                .category(SeatCategory.PREMIUM)
                .build();
        seat = testEntityManager.persistAndFlush(seat);

        Movie movie = Movie.builder()
                .title("Kalamkaval")
                .genre("Thriller")
                .language("malayalam")
                .build();
        movie = testEntityManager.persistAndFlush(movie);

        Show show = Show.builder()
                .extraCharge(10)
                .startTime(LocalDateTime.now().plusHours(10))
                .screen(screen)
                .movie(movie)
                .build();
        show = testEntityManager.persistAndFlush(show);

        ShowSeat showSeat1 = ShowSeat.builder()
                .status(ShowSeatStatus.AVAILABLE)
                .seat(seat)
                .show(show)
                .build();
        showSeat1 = testEntityManager.persistAndFlush(showSeat1);

        ShowSeat showSeat2 = ShowSeat.builder()
                .status(ShowSeatStatus.AVAILABLE)
                .seat(seat)
                .show(show)
                .build();
        showSeat2 = testEntityManager.persistAndFlush(showSeat2);

        ShowSeat showSeat3 = ShowSeat.builder()
                .status(ShowSeatStatus.RESERVED)
                .seat(seat)
                .show(show)
                .build();
        showSeat3 = testEntityManager.persistAndFlush(showSeat3);

        testEntityManager.flush();
        testEntityManager.clear();

        //Act

        List<ShowSeat> availableSeatsFetched = showSeatRepository.findByShowIdAndStatus(show.getId(),ShowSeatStatus.AVAILABLE);

        //assert
        assertNotNull(availableSeatsFetched);
        assertEquals(2,availableSeatsFetched.size());

    }

    @Test
    public  void findAllByIdForUpdate_shouldLockAllShowSeatSuccessfully(){
        Theater theater = Theater.builder()
                .name("jose")
                .location("Mananthavady")
                .build();
        theater = testEntityManager.persistAndFlush(theater);

        Screen screen = Screen.builder()
                .name("screen 1")
                .theater(theater)
                .totalSeats(124)
                .build();
        screen = testEntityManager.persistAndFlush(screen);

        SeatLayout seatLayout = SeatLayout.builder()
                .layoutType(LayoutType.SCREEN_X)
                .screen(screen)
                .build();
        seatLayout = testEntityManager.persistAndFlush(seatLayout);

        SeatRow seatRow = SeatRow.builder()
                .rowLabel("A")
                .seatLayout(seatLayout)
                .build();
        seatRow = testEntityManager.persistAndFlush(seatRow);

        Seat seat = Seat.builder()
                .seatNumber(10)
                .price(100.00)
                .category(SeatCategory.PREMIUM)
                .build();
        seat = testEntityManager.persistAndFlush(seat);

        Movie movie = Movie.builder()
                .title("Kalamkaval")
                .genre("Thriller")
                .language("malayalam")
                .build();
        movie = testEntityManager.persistAndFlush(movie);

        Show show = Show.builder()
                .extraCharge(10)
                .startTime(LocalDateTime.now().plusHours(10))
                .screen(screen)
                .movie(movie)
                .build();
        show = testEntityManager.persistAndFlush(show);

        ShowSeat showSeat1 = ShowSeat.builder()
                .status(ShowSeatStatus.AVAILABLE)
                .seat(seat)
                .show(show)
                .build();
        showSeat1 = testEntityManager.persistAndFlush(showSeat1);

        ShowSeat showSeat2 = ShowSeat.builder()
                .status(ShowSeatStatus.AVAILABLE)
                .seat(seat)
                .show(show)
                .build();
        showSeat2 = testEntityManager.persistAndFlush(showSeat2);

        ShowSeat showSeat3 = ShowSeat.builder()
                .status(ShowSeatStatus.RESERVED)
                .seat(seat)
                .show(show)
                .build();
        showSeat3 = testEntityManager.persistAndFlush(showSeat3);

        testEntityManager.flush();
        testEntityManager.clear();

        //Act
        List<ShowSeat> fetchedShowSeats = showSeatRepository.findAllByIdForUpdate(List.of(showSeat1.getId(),showSeat3.getId()));

        //Assert
        assertNotNull(fetchedShowSeats);
        assertEquals(2,fetchedShowSeats.size());

        final UUID id1 = showSeat1.getId();
        final UUID id2 = showSeat2.getId();
        final UUID id3 = showSeat3.getId();

        assertFalse(fetchedShowSeats.stream().anyMatch(showSeat -> showSeat.getId().equals(id2)));
        assertTrue(fetchedShowSeats.stream().anyMatch(showSeat -> showSeat.getId().equals(id1)));
        assertTrue(fetchedShowSeats.stream().anyMatch(showSeat -> showSeat.getId().equals(id3)));

    }

    @Test
    public void releaseExpired_ShouldReleaseAllTheExpiredReservation() {
        // Arrange
        // 1. Create dependencies (minimal required fields)
        Movie movie = Movie.builder().title("Test Movie").build();
        testEntityManager.persistAndFlush(movie);

        Theater theater = Theater.builder().name("Test Theater").build();
        testEntityManager.persistAndFlush(theater);

        Screen screen = Screen.builder().name("Screen 1").theater(theater).build();
        testEntityManager.persistAndFlush(screen);

        Show show = Show.builder().movie(movie).screen(screen).startTime(LocalDateTime.now()).build();
        testEntityManager.persistAndFlush(show);

        SeatLayout layout = SeatLayout.builder().layoutType(LayoutType.STANDARD).build();
        testEntityManager.persistAndFlush(layout);

        SeatRow row = SeatRow.builder().rowLabel("A").seatLayout(layout).build();
        testEntityManager.persistAndFlush(row);

        Seat seat = Seat.builder().seatNumber(1).seatRow(row).build();
        testEntityManager.persistAndFlush(seat);

        // 2. Create ShowSeats
        // Case 1: Expired Reserved Seat
        ShowSeat expiredSeat = ShowSeat.builder()
                .show(show)
                .seat(seat)
                .status(ShowSeatStatus.RESERVED)
                .reservedUntil(Instant.now().minusSeconds(60)) // Expired 1 min ago
                .build();
        expiredSeat = showSeatRepository.save(expiredSeat);

        // Case 2: Active Reserved Seat (Future)
        ShowSeat activeSeat = ShowSeat.builder()
                .show(show)
                .seat(seat)
                .status(ShowSeatStatus.RESERVED)
                .reservedUntil(Instant.now().plusSeconds(60)) // Expires in 1 min
                .build();
        activeSeat = showSeatRepository.save(activeSeat);

        // Case 3: Booked Seat (Should be ignored)
        ShowSeat bookedSeat = ShowSeat.builder()
                .show(show)
                .seat(seat)
                .status(ShowSeatStatus.BOOKED)
                .build();
        bookedSeat = showSeatRepository.save(bookedSeat);

        // Flush and Clear to ensure DB state
        testEntityManager.flush();
        testEntityManager.clear();

        // Act
        int updatedCount = showSeatRepository.releaseExpired(Instant.now());

        // Assert
        // 1. Verify count
        assertEquals(1, updatedCount, "Should update exactly 1 expired seat");

        // 2. Verify Expired Seat is now AVAILABLE
        ShowSeat updatedExpiredSeat = testEntityManager.find(ShowSeat.class, expiredSeat.getId());
        assertEquals(ShowSeatStatus.AVAILABLE, updatedExpiredSeat.getStatus());
        assertNull(updatedExpiredSeat.getReservedUntil());
        assertNull(updatedExpiredSeat.getBooking());

        // 3. Verify Active Seat is still RESERVED
        ShowSeat updatedActiveSeat = testEntityManager.find(ShowSeat.class, activeSeat.getId());
        assertEquals(ShowSeatStatus.RESERVED, updatedActiveSeat.getStatus());

        // 4. Verify Booked Seat is still BOOKED
        ShowSeat updatedBookedSeat = testEntityManager.find(ShowSeat.class, bookedSeat.getId());
        assertEquals(ShowSeatStatus.BOOKED, updatedBookedSeat.getStatus());
    }

}