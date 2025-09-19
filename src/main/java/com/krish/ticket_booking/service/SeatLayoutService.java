package com.krish.ticket_booking.service;

import com.krish.ticket_booking.dto.request.SeatLayoutRequestDto;
import com.krish.ticket_booking.dto.response.SeatLayoutResponseDto;

import java.util.List;
import java.util.UUID;

public interface SeatLayoutService {
    SeatLayoutResponseDto createLayout(UUID screenId, SeatLayoutRequestDto request);
    SeatLayoutResponseDto getLayout(UUID layoutId);
    List<SeatLayoutResponseDto> listLayoutsByScreen(UUID screenId);
    SeatLayoutResponseDto updateLayout(UUID layoutId, SeatLayoutRequestDto request);
    void deleteLayout(UUID layoutId);
}

