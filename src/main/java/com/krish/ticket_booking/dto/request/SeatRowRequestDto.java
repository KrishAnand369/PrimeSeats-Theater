package com.krish.ticket_booking.dto.request;

import com.krish.ticket_booking.entity.enums.SeatCategory;

import java.util.List;

public record SeatRowRequestDto(
        String rowLabel,
        List<SeatRequestDto> seats

) {
}
