package com.krish.ticket_booking.repository;

import com.krish.ticket_booking.entity.ShowSeat;
import com.krish.ticket_booking.entity.enums.ShowSeatStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, UUID> {
    List<ShowSeat> findByShowIdAndStatus(UUID showId, ShowSeatStatus status);
    // Lock seats for update during reservation (SELECT ... FOR UPDATE)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ShowSeat s where s.id in :ids")
    List<ShowSeat> findAllByIdForUpdate(@Param("ids") List<UUID> ids);

    // For show views, etc.
    List<ShowSeat> findByShow_IdAndStatus(UUID showId, ShowSeatStatus status);

    // Bulk release of expired reservations
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update ShowSeat s 
           set s.status = com.krish.ticket_booking.entity.enums.ShowSeatStatus.AVAILABLE,
               s.booking = null, 
               s.reservedUntil = null
           where s.status = com.krish.ticket_booking.entity.enums.ShowSeatStatus.RESERVED
             and s.reservedUntil < :now
           """)
    int releaseExpired(@Param("now") Instant now);
}
