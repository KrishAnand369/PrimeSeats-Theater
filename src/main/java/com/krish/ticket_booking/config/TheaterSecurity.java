package com.krish.ticket_booking.config;

import com.krish.ticket_booking.entity.Screen;
import com.krish.ticket_booking.entity.Theater;
import com.krish.ticket_booking.entity.User;
import com.krish.ticket_booking.exception.ScreenNotFoundException;
import com.krish.ticket_booking.exception.TheaterNotFoundException;
import com.krish.ticket_booking.exception.UserNotFoundException;
import com.krish.ticket_booking.repository.ScreenRepository;
import com.krish.ticket_booking.repository.TheaterRepository;
import com.krish.ticket_booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TheaterSecurity {
    private final TheaterRepository theaterRepository;
    private final UserRepository userRepository;
    private final ScreenRepository screenRepository;


    public boolean isManagerOfTheater(Authentication authentication, UUID theaterId) {
        String email = authentication.getName();
        User manager = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not Found with id: " + email));
        if (manager == null) return false;

        Theater theater = theaterRepository.findById(theaterId).orElseThrow(()-> new TheaterNotFoundException("Theater not found with id: " + theaterId));
        return theater != null && theater.getManager().getId().equals(manager.getId());
    }
    public boolean isManagerOfTheaterByScreenId(Authentication authentication, UUID screenId) {
        Screen screen = screenRepository.findById(screenId).orElseThrow(()->new ScreenNotFoundException("Screen Not Found with id: "+screenId));
        UUID theaterId = screen.getTheater().getId();
        String email = authentication.getName();
        User manager = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not Found with id: " + email));
        if (manager == null) return false;

        Theater theater = theaterRepository.findById(theaterId).orElseThrow(()-> new TheaterNotFoundException("Theater not found with id: " + theaterId));
        return theater != null && theater.getManager().getId().equals(manager.getId());
    }
}

