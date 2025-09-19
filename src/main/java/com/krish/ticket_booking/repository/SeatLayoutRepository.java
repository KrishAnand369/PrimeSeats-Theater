package com.krish.ticket_booking.repository;

import com.krish.ticket_booking.entity.Seat;
import com.krish.ticket_booking.entity.SeatLayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeatLayoutRepository extends JpaRepository<SeatLayout, UUID> {
    List<SeatLayout> findByScreenId(UUID screenId);
}
