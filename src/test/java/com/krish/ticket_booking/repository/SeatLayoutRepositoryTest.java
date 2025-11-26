package com.krish.ticket_booking.repository;

import com.krish.ticket_booking.entity.Screen;
import com.krish.ticket_booking.entity.SeatLayout;
import com.krish.ticket_booking.entity.Theater;
import com.krish.ticket_booking.entity.enums.LayoutType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class SeatLayoutRepositoryTest {

    @Autowired
    SeatLayoutRepository seatLayoutRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    public void  findByScreenIdFetchesSeatLayoutByScreen(){

        //Arrange
        Theater theater = Theater.builder()
                .name("Jose cinemas")
                .location("Mananthavady")
                .build();

        theater = testEntityManager.persistAndFlush(theater);

        Screen screen =Screen.builder()
                .totalSeats(129)
                .name("Screen 1")
                .theater(theater)
                .build();
        screen = testEntityManager.persistAndFlush(screen);

        SeatLayout seatLayout1 = SeatLayout.builder()
                .layoutType(LayoutType.SCREEN_X)
                .screen(screen)
                .build();

        seatLayout1 = testEntityManager.persistAndFlush(seatLayout1);

//        SeatLayout seatLayout2 = SeatLayout.builder()
//                .layoutType(LayoutType.IMAX)
//                .screen(screen)
//                .build();
//
//        seatLayout2 = testEntityManager.persistAndFlush(seatLayout2);

        testEntityManager.flush();
        testEntityManager.clear();

        //Act

        List<SeatLayout> fetchedLayouts = seatLayoutRepository.findByScreenId(screen.getId());

        //Assert

        assertNotNull(fetchedLayouts);
        assertEquals(1,fetchedLayouts.size());
        assertTrue(fetchedLayouts.stream().anyMatch(seatLayout -> seatLayout.getLayoutType().equals(LayoutType.SCREEN_X)));
        assertFalse(fetchedLayouts.stream().anyMatch(seatLayout -> seatLayout.getLayoutType().equals(LayoutType.IMAX)));



    }
}