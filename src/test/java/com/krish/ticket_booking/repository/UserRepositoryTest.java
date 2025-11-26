package com.krish.ticket_booking.repository;

import com.krish.ticket_booking.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void findByEmail_ShouldFetchUserByEmail(){

        User user = User.builder()
                .email("example@mail.com")
                .name("example")
                .build();

        user = entityManager.persistAndFlush(user);


        entityManager.flush();
        entityManager.clear();

        Optional<User> fetchedUser = userRepository.findByEmail("example@mail.com");

        assertNotNull(fetchedUser);
        assertTrue(fetchedUser.isPresent());
        assertEquals("example@mail.com", fetchedUser.get().getEmail());
    }

}