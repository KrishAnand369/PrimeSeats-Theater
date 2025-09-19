package com.krish.ticket_booking.repository;

import com.krish.ticket_booking.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, UUID> {
    List<Theater> findByLocationIgnoreCase(String location);

    List<Theater> findByManagerId(UUID id);
}
