package com.krish.ticket_booking.dto.response;

import java.util.List;

public record ShowRowResponseDto(
        String rowLabel,
        List<ShowSeatResponseDto> seats
) {}
