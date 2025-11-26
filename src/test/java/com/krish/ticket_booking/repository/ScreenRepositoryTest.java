package com.krish.ticket_booking.repository;

import com.krish.ticket_booking.entity.Screen;
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
class ScreenRepositoryTest {

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public  void findByTheaterIdFetchScreenByTheaterId(){

        //Arrange
        // Arrange - Set up test data

        // Create a Theater entity (required for screen)
        Theater theater = Theater.builder()
                .name("PVR Cinemas")                 // Theater name
                .location("Downtown Mall")           // Theater location
                .build();

        // Persist the theater entity to the database
        theater = entityManager.persistAndFlush(theater);

        // Create a Screen entity with required fields
        Screen screen1 = Screen.builder()
                .name("Screen 1")                    // Screen name
                .totalSeats(150)                     // Total number of seats
                .theater(theater)                    // Associate with theater
                .createdAt(LocalDateTime.now())      // Creation timestamp
                .build();

        // Act - Execute the save operation
        Screen savedScreen1 = screenRepository.save(screen1);

        Screen screen2 = Screen.builder()
                .name("Screen 2")                    // Screen name
                .totalSeats(250)                     // Total number of seats
                .theater(theater)                    // Associate with theater
                .createdAt(LocalDateTime.now())      // Creation timestamp
                .build();

        // Act - Execute the save operation
        Screen savedScreen2 = screenRepository.save(screen2);

        entityManager.flush();
        entityManager.clear();

        //Act

        List<Screen> fetchedScreens = screenRepository.findByTheaterId(theater.getId());
        //Assert

        assertNotNull(fetchedScreens);
        assertEquals(fetchedScreens.size(),2);
        assertTrue(fetchedScreens.stream().anyMatch(screen -> screen.getTheater().getName().equals("PVR Cinemas")));
    }

    @Test
    void save_ShouldSaveScreenSuccessfully() {
        // Arrange - Set up test data
        
        // Create a Theater entity (required for screen)
        Theater theater = Theater.builder()
                .name("PVR Cinemas")                 // Theater name
                .location("Downtown Mall")           // Theater location
                .build();
        
        // Persist the theater entity to the database
        theater = entityManager.persistAndFlush(theater);
        
        // Create a Screen entity with required fields
        Screen screen = Screen.builder()
                .name("Screen 1")                    // Screen name
                .totalSeats(150)                     // Total number of seats
                .theater(theater)                    // Associate with theater
                .createdAt(LocalDateTime.now())      // Creation timestamp
                .build();
        
        // Act - Execute the save operation
        Screen savedScreen = screenRepository.save(screen);
        
        // Flush changes to the database
        entityManager.flush();
        
        // Clear the persistence context to ensure fresh read from database
        entityManager.clear();
        
        // Assert - Verify the results
        
        // Verify the screen was saved and has an ID
        assertNotNull(savedScreen);
        assertNotNull(savedScreen.getId());
        
        // Verify the screen name was saved correctly
        assertEquals("Screen 1", savedScreen.getName());
        
        // Verify the total seats was saved correctly
        assertEquals(150, savedScreen.getTotalSeats());
        
        // Verify the theater association was saved correctly
        assertNotNull(savedScreen.getTheater());
        assertEquals(theater.getId(), savedScreen.getTheater().getId());
        assertEquals("PVR Cinemas", savedScreen.getTheater().getName());
        
        // Verify the created timestamp was saved correctly
        assertNotNull(savedScreen.getCreatedAt());
        
        // Verify the screen can be retrieved from the database
        Screen retrievedScreen = entityManager.find(Screen.class, savedScreen.getId());
        assertNotNull(retrievedScreen);
        assertEquals(savedScreen.getId(), retrievedScreen.getId());
        assertEquals("Screen 1", retrievedScreen.getName());
    }

    @Test
    public  void findByTheaterId_ShouldReturnEmptyList_WhenTheaterHasNoScreens(){

        //Arrange
        // Arrange - Set up test data

        // Create a Theater entity (required for screen)
        Theater theater = Theater.builder()
                .name("PVR Cinemas")                 // Theater name
                .location("Downtown Mall")           // Theater location
                .build();

        // Persist the theater entity to the database
        theater = entityManager.persistAndFlush(theater);

        entityManager.flush();
        entityManager.clear();

        //Act

        List<Screen> fetchedScreens = screenRepository.findByTheaterId(theater.getId());
        //Assert

        assertNotNull(fetchedScreens);
        assertTrue(fetchedScreens.isEmpty());
    }
}