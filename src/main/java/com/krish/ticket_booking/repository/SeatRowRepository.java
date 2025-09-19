package com.krish.ticket_booking.repository;

import com.krish.ticket_booking.entity.SeatRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SeatRowRepository extends JpaRepository<SeatRow, UUID> {
}
