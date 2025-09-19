package com.krish.ticket_booking.controller;

import com.krish.ticket_booking.dto.request.ShowRegisterRequestDto;
import com.krish.ticket_booking.dto.response.ShowResponseDto;
import com.krish.ticket_booking.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/screens")
public class ScreenController {
    private final ShowService showService;

    @PostMapping("/{screenId}/shows")
    @PreAuthorize("@theaterSecurity.isManagerOfTheaterByScreenId(authentication, #screenId)")
    public ResponseEntity<ShowResponseDto> createShow(@RequestBody ShowRegisterRequestDto request,@PathVariable UUID screenId) {
        return new ResponseEntity<>(showService.createShow(request,screenId), HttpStatus.CREATED);
    }

    @PutMapping("/{screenId}/shows/{showId}")
    @PreAuthorize("@theaterSecurity.isManagerOfTheaterByScreenId(authentication, #screenId)")
    public ResponseEntity<ShowResponseDto> updateShow(@RequestBody ShowRegisterRequestDto request,@PathVariable UUID screenId,@PathVariable UUID showId) {
        return ResponseEntity.ok(showService.updateShow(request,screenId,showId));
    }

    @GetMapping("/{screenId}/shows")
    public ResponseEntity<List<ShowResponseDto>> listShows(@PathVariable UUID screenId) {
        return new ResponseEntity<>(showService.listShowsByScreenId(screenId), HttpStatus.OK);
    }


}


