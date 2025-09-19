package com.krish.ticket_booking.service;

import com.krish.ticket_booking.dto.request.ShowRegisterRequestDto;
import com.krish.ticket_booking.dto.response.ShowLayoutResponseDto;
import com.krish.ticket_booking.dto.response.ShowResponseDto;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface ShowService {
    ShowResponseDto createShow(ShowRegisterRequestDto request,UUID screenId);
    ShowResponseDto getShow(UUID showId);
    ShowResponseDto updateShow(ShowRegisterRequestDto request,UUID screenId,UUID showId);
    List<ShowResponseDto> getListOfShowsByManager(Authentication authentication);
    ShowLayoutResponseDto getShowLayout(UUID showId);
    List<UUID> getShowBookedSeats(UUID showId);
    List<ShowResponseDto> listShowsByScreenId(UUID screenId);
}

