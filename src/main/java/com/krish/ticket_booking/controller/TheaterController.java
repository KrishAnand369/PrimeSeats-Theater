package com.krish.ticket_booking.controller;

import com.krish.ticket_booking.dto.request.ScreenRegisterRequest;
import com.krish.ticket_booking.dto.request.TheaterRegisterRequest;
import com.krish.ticket_booking.dto.response.ScreenResponse;
import com.krish.ticket_booking.dto.response.TheaterResponseDto;
import com.krish.ticket_booking.service.ScreenService;
import com.krish.ticket_booking.service.TheaterService;
import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/theaters")
@RequiredArgsConstructor
public class TheaterController {

    public final TheaterService theaterService;
    public final ScreenService screenService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TheaterResponseDto> create(@Valid @RequestBody TheaterRegisterRequest dto) {
        return new ResponseEntity<>(theaterService.createTheater(dto), HttpStatus.CREATED);
    }

    //@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping
    public ResponseEntity<List<TheaterResponseDto>> listTheaters(
            @RequestParam(value = "location", required = false) String location

    ){
        return ResponseEntity.ok(theaterService.getListOfTheaters(location));
    }

    @PutMapping({"/{theaterId}/assignManager"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignManagerForTheater(@PathVariable UUID theaterId,@Valid @RequestParam (value = "manager", required = false) UUID managerId){
        try {
            theaterService.assignTheaterManager(theaterId,managerId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("Error while assigning manager to theater");
        }
    }

    @PostMapping("/{theaterId}/screens")
    @PreAuthorize("@theaterSecurity.isManagerOfTheater(authentication, #theaterId)")
    public ResponseEntity<ScreenResponse> createScreen(@Valid @RequestBody ScreenRegisterRequest request ,@PathVariable UUID theaterId){

        try {
            return new ResponseEntity<>(screenService.createScreen(request,theaterId),HttpStatus.CREATED);
        } catch (Exception e) {
            throw new RuntimeException("Error while Creating the Screen");
        }
    }

    @GetMapping("/{theaterId}/screens")
    public ResponseEntity<List<ScreenResponse>> listScreensByTheater(@PathVariable UUID theaterId){

        try {
            return new ResponseEntity<>(screenService.listScreenByTheaterId(theaterId),HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("Error while Creating the Screen");
        }
    }
}
