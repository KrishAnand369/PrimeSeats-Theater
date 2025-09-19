package com.krish.ticket_booking.dto.response;

import java.util.List;
import java.util.UUID;
import com.krish.ticket_booking.entity.QrCode;

public record TicketResponse(
        UUID ticketId,
        UUID bookingId,
        UUID showId,
        List<String> seatLabels,
        double amount,
        QrCode qrCode
) {
}
