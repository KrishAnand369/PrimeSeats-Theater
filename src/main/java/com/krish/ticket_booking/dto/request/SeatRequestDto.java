package com.krish.ticket_booking.dto.request;

import com.krish.ticket_booking.entity.enums.SeatCategory;

public record SeatRequestDto(
        int seatNumber,
        SeatCategory category,
        Double price
) {
}
