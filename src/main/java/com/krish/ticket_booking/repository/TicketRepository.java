package com.krish.ticket_booking.repository;

import com.krish.ticket_booking.entity.BookingSeat;
import com.krish.ticket_booking.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
}
