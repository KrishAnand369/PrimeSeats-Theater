package com.krish.ticket_booking.controller;

import com.krish.ticket_booking.dto.request.StaffRegisterRequest;
import com.krish.ticket_booking.exception.EmailAlreadyExistsException;
import com.krish.ticket_booking.exception.InternalException;
import com.krish.ticket_booking.service.UserService;
import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @PostMapping("staffs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody StaffRegisterRequest req) {

        try {
            userService.createUserWithRole(req);
            return new ResponseEntity<>(HttpStatus.CREATED);

        }catch (EmailAlreadyExistsException e) {
            throw new EmailAlreadyExistsException(e.getMessage());
        }
        catch (Exception e) {
            throw new InternalException("Error While Saving the user");
        }
    }

    //@PostMapping("theater")
    //public
//    @GetMapping("theater/list")
//    //@PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<> listTheaters(){
//
//    }
}
