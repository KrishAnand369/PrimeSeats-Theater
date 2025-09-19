package com.krish.ticket_booking.jobs;

import com.krish.ticket_booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationExpiryJob {

    private final BookingService bookingService;

    // Every 60 seconds
    @Scheduled(fixedRate = 60_000L)
    public void releaseExpired() {
        int count = bookingService.releaseExpiredReservations();
        if (count > 0) {
            log.info("Released {} expired seat reservations", count);
        }
    }
}
