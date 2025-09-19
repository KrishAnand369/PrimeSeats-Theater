package com.krish.ticket_booking.service.impl;

import com.krish.ticket_booking.dto.request.SeatLayoutRequestDto;
import com.krish.ticket_booking.dto.request.SeatRequestDto;
import com.krish.ticket_booking.dto.request.SeatRowRequestDto;
import com.krish.ticket_booking.entity.Screen;
import com.krish.ticket_booking.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class SeatLayoutValidator {

    public void validateLayout(SeatLayoutRequestDto layoutDTO, Screen screen) {
        validateRowLabels(layoutDTO);
        validateSeatNumbers(layoutDTO);
        validateCapacity(layoutDTO, screen);
        validatePricing(layoutDTO);
    }

    private void validateRowLabels(SeatLayoutRequestDto layoutDTO) {
        Set<String> rowLabels = new HashSet<>();
        for (SeatRowRequestDto row : layoutDTO.rows()) {
            String label = row.rowLabel().toUpperCase();
            if (!rowLabels.add(label)) {
                throw new BusinessException("Duplicate row label: " + row.rowLabel());
            }
            if (row.rowLabel().length() != 1) {
                throw new BusinessException("Row label must be a single character: " + row.rowLabel());
            }
        }
    }

    private void validateSeatNumbers(SeatLayoutRequestDto layoutDTO) {
        for (SeatRowRequestDto row : layoutDTO.rows()) {
            Set<Integer> seatNumbers = new HashSet<>();
            for (SeatRequestDto seat : row.seats()) {
                if (!seatNumbers.add(seat.seatNumber())) {
                    throw new BusinessException("Duplicate seat number " + seat.seatNumber() +
                            " in row " + row.rowLabel());
                }
            }
        }
    }

    private void validateCapacity(SeatLayoutRequestDto layoutDTO, Screen screen) {
        int totalSeats = layoutDTO.rows().stream()
                .mapToInt(row -> row.seats().size())
                .sum();

        if (totalSeats > screen.getTotalSeats()) {
            throw new BusinessException("Layout has " + totalSeats +
                    " seats but screen capacity is only " + screen.getTotalSeats());
        }
    }

    private void validatePricing(SeatLayoutRequestDto layoutDTO) {
        for (SeatRowRequestDto row : layoutDTO.rows()) {
            for (SeatRequestDto seat : row.seats()) {
                if (seat.price() == null || seat.price() <= 0) {
                    throw new BusinessException("Invalid price for seat " +
                            row.rowLabel() + seat.seatNumber());
                }
            }
        }
    }
}
