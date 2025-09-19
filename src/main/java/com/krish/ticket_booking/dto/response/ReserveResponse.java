package com.krish.ticket_booking.dto.response;

import com.krish.ticket_booking.entity.enums.BookingStatusEnum;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ReserveResponse(
        UUID bookingId,
        BookingStatusEnum status,
        Instant reservedUntil,
        List<UUID> seatIds,
        double totalAmount
) {
}
