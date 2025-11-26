package com.krish.ticket_booking.repository;

import com.krish.ticket_booking.entity.Seat;
import com.krish.ticket_booking.entity.SeatLayout;
import com.krish.ticket_booking.entity.SeatRow;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static com.krish.ticket_booking.entity.enums.LayoutType.STANDARD;
import static com.krish.ticket_booking.entity.enums.SeatCategory.PREMIUM;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class SeatRepositoryTest {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void save_ShouldSaveSeatSuccessfully(){

        //Arrange
        SeatLayout seatLayout = SeatLayout.builder()
                .layoutType(STANDARD).build();
        seatLayout = testEntityManager.persistAndFlush(seatLayout);

        SeatRow seatRow = SeatRow.builder()
                .rowLabel("A")
                .seatLayout(seatLayout)
                .build();
        seatRow = testEntityManager.persistAndFlush(seatRow);

        Seat seat = Seat.builder()
                .category(PREMIUM)
                .seatNumber(10)
                .price(100.00)
                .seatRow(seatRow)
                .build();
        seat = testEntityManager.persistAndFlush(seat);

        testEntityManager.flush();
        testEntityManager.clear();

        //Act

        Seat savedSeat  = seatRepository.save(seat);

        testEntityManager.flush();
        testEntityManager.clear();

        //Assert

        assertNotNull(savedSeat);
        assertNotNull(savedSeat.getId());
        assertEquals(10, savedSeat.getSeatNumber());
        assertEquals(100.0, savedSeat.getPrice());

        // Verify the SeatRow relationship
        assertNotNull(savedSeat.getSeatRow());
        assertEquals("A", savedSeat.getSeatRow().getRowLabel());

        // Verify the SeatLayout relationship (through SeatRow)
        assertNotNull(savedSeat.getSeatRow().getSeatLayout());
        assertEquals(STANDARD, savedSeat.getSeatRow().getSeatLayout().getLayoutType());

    }

    @Test
    public  void findAllByIdForUpdate_ShouldLockAndFetchSeats(){

        //Arrange
        SeatLayout seatLayout = SeatLayout.builder()
                .layoutType(STANDARD).build();
        seatLayout = testEntityManager.persistAndFlush(seatLayout);

        SeatRow seatRow = SeatRow.builder()
                .rowLabel("A")
                .seatLayout(seatLayout)
                .build();
        seatRow = testEntityManager.persistAndFlush(seatRow);

        Seat seat1 = Seat.builder()
                .category(PREMIUM)
                .seatNumber(10)
                .price(100.00)
                .seatRow(seatRow)
                .build();
        seat1 = testEntityManager.persistAndFlush(seat1);

        Seat seat2 = Seat.builder()
                .category(PREMIUM)
                .seatNumber(11)
                .price(100.00)
                .seatRow(seatRow)
                .build();
        seat2 = testEntityManager.persistAndFlush(seat2);

        testEntityManager.flush();
        testEntityManager.clear();
        //Action
        List<Seat>fetchedSeats=seatRepository.findAllByIdForUpdate(List.of(seat1.getId(),seat2.getId()));

        assertNotNull(fetchedSeats);
        assertEquals(fetchedSeats.size(),2);
        assertTrue(fetchedSeats.stream().anyMatch(s -> s.getSeatNumber() == 10));
        assertTrue(fetchedSeats.stream().anyMatch(s -> s.getSeatNumber() == 11));
        assertEquals(fetchedSeats.getFirst().getSeatRow().getRowLabel(),"A");

    }

}