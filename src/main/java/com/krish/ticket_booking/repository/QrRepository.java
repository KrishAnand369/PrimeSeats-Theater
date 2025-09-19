package com.krish.ticket_booking.repository;

import com.krish.ticket_booking.entity.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QrRepository extends JpaRepository<QrCode, UUID> {
}
