// Package declaration for the test class
package com.krish.ticket_booking.service.impl;

// Import the request DTO used for staff registration
import com.krish.ticket_booking.dto.request.StaffRegisterRequest;
// Import the User entity class
import com.krish.ticket_booking.entity.User;
// Import the role enumeration
import com.krish.ticket_booking.entity.enums.RoleEnum;
// Import the custom exception for duplicate email
import com.krish.ticket_booking.exception.EmailAlreadyExistsException;
// Import the user repository interface
import com.krish.ticket_booking.repository.UserRepository;
// Import JUnit 5 Test annotation
import org.junit.jupiter.api.Test;
// Import Mockito extension for JUnit 5
import org.junit.jupiter.api.extension.ExtendWith;
// Import annotation to inject mocks into the test subject
import org.mockito.InjectMocks;
// Import annotation to create mock objects
import org.mockito.Mock;
// Import Mockito's JUnit 5 extension
import org.mockito.junit.jupiter.MockitoExtension;
// Import Spring Security's password encoder
import org.springframework.security.crypto.password.PasswordEncoder;

// Import Optional for handling nullable values
import java.util.Optional;

// Import JUnit assertions for test validation
import static org.junit.jupiter.api.Assertions.*;
// Import Mockito's any() matcher for flexible argument matching
import static org.mockito.ArgumentMatchers.any;
// Import Mockito's anyString() matcher for string arguments
import static org.mockito.ArgumentMatchers.anyString;
// Import Mockito's static methods for mocking and verification
import static org.mockito.Mockito.*;

// Enable Mockito annotations for this test class
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    // Create a mock instance of UserRepository
    @Mock
    private UserRepository userRepository;

    // Create a mock instance of PasswordEncoder
    @Mock
    private PasswordEncoder passwordEncoder;

    // Create an instance of UserServiceImpl with mocks injected
    @InjectMocks
    private UserServiceImpl userService;

    // Test method to verify successful user creation with role
    @Test
    void createUserWithRole_ShouldCreateUserSuccessfully() {
        // Arrange - Set up test data and mock behavior
        
        // Create a staff registration request with test data
        StaffRegisterRequest request = new StaffRegisterRequest(
                "John Doe",                    // User's full name
                "john.doe@example.com",        // User's email address
                "password123",                 // Plain text password
                RoleEnum.MANAGER               // User's role (changed from STAFF to MANAGER)
        );

        // Define the expected encoded password
        String encodedPassword = "encodedPassword123";

        // Mock the repository to return empty Optional (email not found)
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        // Mock the password encoder to return the encoded password
        when(passwordEncoder.encode(request.password())).thenReturn(encodedPassword);
        // Mock the repository save to return the same user object
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act - Execute the method under test
        User result = userService.createUserWithRole(request);

        // Assert - Verify the results
        
        // Verify the result is not null
        assertNotNull(result);
        // Verify the user's name matches the request
        assertEquals(request.name(), result.getName());
        // Verify the user's email matches the request
        assertEquals(request.email(), result.getEmail());
        // Verify the password was encoded correctly
        assertEquals(encodedPassword, result.getPassword());
        // Verify the user's role matches the request
        assertEquals(request.role(), result.getRole());

        // Verify that findByEmail was called with the correct email
        verify(userRepository).findByEmail(request.email());
        // Verify that password encoding was performed
        verify(passwordEncoder).encode(request.password());
        // Verify that the user was saved to the repository
        verify(userRepository).save(any(User.class));
    }

    // Test method to verify exception is thrown when email already exists
    @Test
    void createUserWithRole_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange - Set up test data and mock behavior
        
        // Create a staff registration request with test data
        StaffRegisterRequest request = new StaffRegisterRequest(
                "Jane Doe",                    // User's full name
                "jane.doe@example.com",        // User's email address (duplicate)
                "password456",                 // Plain text password
                RoleEnum.ADMIN                 // User's role
        );

        // Create an existing user with the same email
        User existingUser = User.builder()
                .name("Existing User")         // Existing user's name
                .email(request.email())        // Same email as the request
                .password("existingPassword")  // Existing user's password
                .role(RoleEnum.MANAGER)        // Existing user's role (changed from STAFF to MANAGER)
                .build();

        // Mock the repository to return the existing user (email found)
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(existingUser));

        // Act & Assert - Execute and verify exception is thrown
        
        // Verify that EmailAlreadyExistsException is thrown
        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,  // Expected exception type
                () -> userService.createUserWithRole(request)  // Method call that should throw
        );

        // Verify the exception message is correct
        assertEquals("Email is already registered", exception.getMessage());
        // Verify that findByEmail was called
        verify(userRepository).findByEmail(request.email());
        // Verify that password encoding was never called
        verify(passwordEncoder, never()).encode(anyString());
        // Verify that save was never called
        verify(userRepository, never()).save(any(User.class));
    }
}