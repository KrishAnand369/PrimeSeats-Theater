package com.krish.ticket_booking.dto.request;

import com.krish.ticket_booking.entity.enums.LayoutType;

import java.util.List;

public record SeatLayoutRequestDto(

        LayoutType layoutType,
        List<SeatRowRequestDto> rows
) {
}
