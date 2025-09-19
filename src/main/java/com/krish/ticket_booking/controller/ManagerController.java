package com.krish.ticket_booking.controller;

import com.krish.ticket_booking.dto.request.ScreenRegisterRequest;
import com.krish.ticket_booking.dto.request.StaffRegisterRequest;
import com.krish.ticket_booking.dto.response.ShowResponseDto;
import com.krish.ticket_booking.dto.response.TheaterResponseDto;
import com.krish.ticket_booking.exception.EmailAlreadyExistsException;
import com.krish.ticket_booking.exception.InternalException;
import com.krish.ticket_booking.service.ShowService;
import com.krish.ticket_booking.service.TheaterService;
import com.krish.ticket_booking.service.UserService;
import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class ManagerController {

    private final UserService userService;
    private final TheaterService theaterService;
    private final ShowService showService;

    @GetMapping("/theaters")
    public ResponseEntity<List<TheaterResponseDto>> listManagedTheaters(
            @RequestParam(value = "location", required = false) String location

    ){
        return ResponseEntity.ok(theaterService.getListOfTheatersByManager(SecurityContextHolder.getContext().getAuthentication()));
    }
    @GetMapping("/shows")
    public ResponseEntity<List<ShowResponseDto>> listManagedShows(
            @RequestParam(value = "location", required = false) String location

    ){
        return ResponseEntity.ok(showService.getListOfShowsByManager(SecurityContextHolder.getContext().getAuthentication()));
    }

}
