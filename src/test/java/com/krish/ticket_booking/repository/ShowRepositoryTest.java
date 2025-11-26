package com.krish.ticket_booking.repository;

import com.krish.ticket_booking.entity.Movie;
import com.krish.ticket_booking.entity.Screen;
import com.krish.ticket_booking.entity.Show;
import com.krish.ticket_booking.entity.Theater;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ShowRepositoryTest {

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void findByScreenId_FetchShowsByScreenId(){

        //Arrange
        Movie movie = Movie.builder()
                .title("KalamKaval")
                .genre("Thriller")
                .language("Malayalam")
                .duration(148)                       // Duration in minutes
                .active(true)                        // Movie is active
                .build();
        movie = testEntityManager.persistAndFlush(movie);

        Theater theater = Theater.builder()
                .name("Jose Cinemas")
                .location("Mananathavdy")
                .build();

        theater = testEntityManager.persistAndFlush(theater);

        Screen screen1 = Screen.builder()
                .name("Screen 1")
                .theater(theater)
                .totalSeats(328)
                .build();

        screen1 = testEntityManager.persistAndFlush(screen1);

        Screen screen2 = Screen.builder()
                .name("Screen 2")
                .theater(theater)
                .totalSeats(246)
                .build();

        screen2 = testEntityManager.persistAndFlush(screen2);


        Show show1 = Show.builder()
                .extraCharge(10)
                .startTime(LocalDateTime.now().plusDays(1))  // Show start time (tomorrow)
                .movie(movie)
                .screen(screen1)
                .build();

        show1 = testEntityManager.persistAndFlush(show1);

        Show show2 = Show.builder()
                .extraCharge(12)
                .startTime(LocalDateTime.now().plusHours(2))  // Show start time (tomorrow)
                .movie(movie)
                .screen(screen1)
                .build();

        show2 = testEntityManager.persistAndFlush(show2);

        Show show3 = Show.builder()
                .extraCharge(0)
                .startTime(LocalDateTime.now().plusDays(2))  // Show start time (tomorrow)
                .movie(movie)
                .screen(screen2)
                .build();

        show3 = testEntityManager.persistAndFlush(show3);

        testEntityManager.flush();
        testEntityManager.clear();

        //Act

        List<Show> fetchedShowsScreen1 = showRepository.findByScreenId(screen1.getId());
        List<Show> fetchedShowsScreen2 = showRepository.findByScreenId(screen2.getId());

        //Assert

        assertNotNull(fetchedShowsScreen1);
        assertEquals(2,fetchedShowsScreen1.size());
        assertTrue(fetchedShowsScreen1.stream().anyMatch(show -> show.getExtraCharge()==12));

        assertNotNull(fetchedShowsScreen2);
        assertEquals(1,fetchedShowsScreen2.size());
        assertTrue(fetchedShowsScreen2.stream().anyMatch(show -> show.getExtraCharge()==0));





    }
}