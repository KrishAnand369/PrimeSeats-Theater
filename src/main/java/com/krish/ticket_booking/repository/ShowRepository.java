package com.krish.ticket_booking.repository;

import com.krish.ticket_booking.entity.Show;
import com.krish.ticket_booking.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShowRepository extends JpaRepository<Show, UUID> {

    List<Show> findByScreenId(UUID id);
}
