package com.krish.ticket_booking.controller;

import com.krish.ticket_booking.dto.request.SeatLayoutRequestDto;
import com.krish.ticket_booking.dto.response.SeatLayoutResponseDto;
import com.krish.ticket_booking.service.SeatLayoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/screens/{screenId}/layouts")
@RequiredArgsConstructor
public class SeatLayoutController {

    private final SeatLayoutService layoutService;

    @PostMapping
    @PreAuthorize("@theaterSecurity.isManagerOfTheaterByScreenId(authentication, #screenId)")
    public ResponseEntity<SeatLayoutResponseDto> createLayout(
            @PathVariable UUID screenId,
            @Valid @RequestBody SeatLayoutRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(layoutService.createLayout(screenId, request));
    }

    @GetMapping("/{layoutId}")
    public ResponseEntity<SeatLayoutResponseDto> getLayout(@PathVariable UUID layoutId) {
        return ResponseEntity.ok(layoutService.getLayout(layoutId));
    }

    @GetMapping
    @PreAuthorize("@theaterSecurity.isManagerOfTheaterByScreenId(authentication, #screenId)")
    public ResponseEntity<List<SeatLayoutResponseDto>> listLayouts(@PathVariable UUID screenId) {
        return ResponseEntity.ok(layoutService.listLayoutsByScreen(screenId));
    }

    @PutMapping("/{layoutId}")
    @PreAuthorize("@theaterSecurity.isManagerOfTheaterByScreenId(authentication, #screenId)")
    public ResponseEntity<SeatLayoutResponseDto> updateLayout(
            @PathVariable UUID layoutId,
            @Valid @RequestBody SeatLayoutRequestDto request) {
        return ResponseEntity.ok(layoutService.updateLayout(layoutId, request));
    }

    @DeleteMapping("/{layoutId}")
    @PreAuthorize("@theaterSecurity.isManagerOfTheaterByScreenId(authentication, #screenId)")
    public ResponseEntity<Void> deleteLayout(@PathVariable UUID layoutId) {
        layoutService.deleteLayout(layoutId);
        return ResponseEntity.noContent().build();
    }

}

