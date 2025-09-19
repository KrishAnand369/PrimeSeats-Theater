package com.krish.ticket_booking.dto.response;

import com.krish.ticket_booking.entity.enums.SeatCategory;

import java.util.UUID;

public record SeatResponseDto(
        UUID id,
        String seatLabel,
        SeatCategory category,
        Double price
) {}
