package com.krish.ticket_booking.repository;

import com.krish.ticket_booking.entity.Booking;
import com.krish.ticket_booking.entity.Movie;
import com.krish.ticket_booking.entity.Show;
import com.krish.ticket_booking.entity.enums.BookingStatusEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void save_ShouldSaveBookingSuccessfully() {
        // Arrange - Set up test data
        
        // Create a Movie entity (required for show)
        Movie movie = Movie.builder()
                .title("Inception")                  // Movie title
                .genre("Sci-Fi")                     // Movie genre
                .language("English")                 // Movie language
                .duration(148)                       // Duration in minutes
                .active(true)                        // Movie is active
                .build();
        
        // Persist the movie entity to the database
        movie = entityManager.persistAndFlush(movie);
        
        // Create a Show entity (required for booking)
        Show show = Show.builder()
                .movie(movie)                        // Associate with movie
                .startTime(LocalDateTime.now().plusDays(1))  // Show start time (tomorrow)
                .extraCharge(50.0)                   // Extra charge for this show
                .build();
        
        // Persist the show entity to the database
        show = entityManager.persistAndFlush(show);
        
        // Create a Booking entity with required fields
        Booking booking = Booking.builder()
                .status(BookingStatusEnum.PENDING_PAYMENT)  // Initial status
                .totalAmount(250.0)                  // Total booking amount
                .show(show)                          // Associate with the show
                .guestEmail("guest@example.com")     // Guest email (no user account)
                .createdAt(LocalDateTime.now())      // Booking creation timestamp
                .build();
        
        // Act - Execute the save operation
        Booking savedBooking = bookingRepository.save(booking);
        
        // Flush changes to the database
        entityManager.flush();
        
        // Clear the persistence context to ensure fresh read from database
        entityManager.clear();
        
        // Assert - Verify the results
        
        // Verify the booking was saved and has an ID
        assertNotNull(savedBooking);
        assertNotNull(savedBooking.getId());
        
        // Verify the status was saved correctly
        assertEquals(BookingStatusEnum.PENDING_PAYMENT, savedBooking.getStatus());
        
        // Verify the total amount was saved correctly
        assertEquals(250.0, savedBooking.getTotalAmount());
        
        // Verify the guest email was saved correctly
        assertEquals("guest@example.com", savedBooking.getGuestEmail());
        
        // Verify the show association was saved correctly
        assertNotNull(savedBooking.getShow());
        assertEquals(show.getId(), savedBooking.getShow().getId());
        
        // Verify the movie association through show
        assertNotNull(savedBooking.getShow().getMovie());
        assertEquals(movie.getId(), savedBooking.getShow().getMovie().getId());
        assertEquals("Inception", savedBooking.getShow().getMovie().getTitle());
        
        // Verify the created timestamp was saved correctly
        assertNotNull(savedBooking.getCreatedAt());
        
        // Verify the booking can be retrieved from the database
        Booking retrievedBooking = entityManager.find(Booking.class, savedBooking.getId());
        assertNotNull(retrievedBooking);
        assertEquals(savedBooking.getId(), retrievedBooking.getId());
    }
}