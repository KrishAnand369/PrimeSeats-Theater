package com.krish.ticket_booking.repository;

import com.krish.ticket_booking.entity.Booking;
import com.krish.ticket_booking.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, UUID> {
}
