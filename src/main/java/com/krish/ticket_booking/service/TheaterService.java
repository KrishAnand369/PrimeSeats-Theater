package com.krish.ticket_booking.service;

import com.krish.ticket_booking.dto.request.TheaterRegisterRequest;
import com.krish.ticket_booking.dto.response.TheaterResponseDto;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface TheaterService {

    TheaterResponseDto createTheater(TheaterRegisterRequest request);

    List<TheaterResponseDto> getListOfTheaters(String location);

    void assignTheaterManager(UUID theaterId,UUID userId);

    List<TheaterResponseDto> getListOfTheatersByManager(Authentication authentication);
}
