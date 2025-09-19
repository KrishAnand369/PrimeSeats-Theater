package com.krish.ticket_booking.controller;

import com.krish.ticket_booking.dto.request.ShowRegisterRequestDto;
import com.krish.ticket_booking.dto.response.ShowLayoutResponseDto;
import com.krish.ticket_booking.dto.response.ShowResponseDto;
import com.krish.ticket_booking.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shows")
public class ShowController {
    private final ShowService showService;

    @GetMapping("/{showId}")
    public ResponseEntity<ShowResponseDto> getShow(@PathVariable UUID showId) {
        return ResponseEntity.ok(showService.getShow(showId));
    }

    @GetMapping("/{showId}/layout")
    public ResponseEntity<ShowLayoutResponseDto> getShowLayout(@PathVariable UUID showId) {
        return ResponseEntity.ok(showService.getShowLayout(showId));
    }

    @GetMapping("/{showId}/bookedSeats")
    public ResponseEntity<List<UUID>> getShowBookedSeats(@PathVariable UUID showId) {
        return ResponseEntity.ok(showService.getShowBookedSeats(showId));
    }


}


