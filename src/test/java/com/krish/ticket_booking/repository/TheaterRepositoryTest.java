package com.krish.ticket_booking.repository;

import com.krish.ticket_booking.entity.Theater;
import com.krish.ticket_booking.entity.User;
import com.krish.ticket_booking.entity.enums.RoleEnum;
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
class TheaterRepositoryTest {

    @Autowired
    private  TestEntityManager testEntityManager;

    @Autowired
    private TheaterRepository theaterRepository;

    @Test
    public void findByLocationIgnoreCase_FetchTheatersByLocationSuccessfully(){

        Theater theater1 = Theater.builder()
                .name("Maruthi E Cinemas")
                .location("Mananthavady")
                .build();

        theater1 = testEntityManager.persistAndFlush(theater1);

        Theater theater2 = Theater.builder()
                .name("Jose Cinemas")
                .location("Mananthavady")
                .build();

        theater2 = testEntityManager.persistAndFlush(theater2);

        Theater theater3 = Theater.builder()
                .name("Jaithra")
                .location("Kalpetta")
                .build();

        theater3 = testEntityManager.persistAndFlush(theater3);

        testEntityManager.flush();
        testEntityManager.clear();

        List<Theater> fetchedTheaters = theaterRepository.findByLocationIgnoreCase("ManAnthavady");

        assertNotNull(fetchedTheaters);
        assertEquals(2,fetchedTheaters.size());
        assertTrue(fetchedTheaters.stream().anyMatch(t->t.getName().equals("Jose Cinemas")));
        assertFalse(fetchedTheaters.stream().anyMatch(t->t.getName().equals("Jaithra")));

    }

    @Test
    public void findByManagerIdIgnoreCase_FetchTheatersByManagerSuccessfully(){

        User mgr1 = User.builder()
                .email("mg1@mail.com")
                .role(RoleEnum.MANAGER)
                .name("josettan")
                .build();

        testEntityManager.persistAndFlush(mgr1);

        User mgr2 = User.builder()
                .email("mg2@mail.com")
                .role(RoleEnum.MANAGER)
                .name("mohanan")
                .build();

        testEntityManager.persistAndFlush(mgr2);


        Theater theater1 = Theater.builder()
                .name("Maruthi E Cinemas")
                .location("Mananthavady")
                .manager(mgr1)
                .build();

        theater1 = testEntityManager.persistAndFlush(theater1);

        Theater theater2 = Theater.builder()
                .name("Jose Cinemas")
                .location("Mananthavady")
                .manager(mgr2)
                .build();

        theater2 = testEntityManager.persistAndFlush(theater2);

        Theater theater3 = Theater.builder()
                .name("Jaithra")
                .location("Kalpetta")
                .manager(mgr1)
                .build();

        theater3 = testEntityManager.persistAndFlush(theater3);

        testEntityManager.flush();
        testEntityManager.clear();

        List<Theater> fetchedTheaters1 = theaterRepository.findByManagerId(mgr1.getId());
        List<Theater> fetchedTheaters2 = theaterRepository.findByManagerId(mgr2.getId());

        assertNotNull(fetchedTheaters1);
        assertNotNull(fetchedTheaters2);
        assertEquals(2,fetchedTheaters1.size());
        assertEquals(1,fetchedTheaters2.size());
        assertTrue(fetchedTheaters2.stream().anyMatch(t->t.getName().equals("Jose Cinemas")));
        assertFalse(fetchedTheaters2.stream().anyMatch(t->t.getName().equals("Jaithra")));

    }

}