package com.krish.ticket_booking.dto.response;

import com.krish.ticket_booking.entity.enums.SeatCategory;
import com.krish.ticket_booking.entity.enums.ShowSeatStatus;

import java.util.UUID;

public record ShowSeatResponseDto(
        UUID id,
        String seatLabel,
        ShowSeatStatus status,
        Double price
) {
}
