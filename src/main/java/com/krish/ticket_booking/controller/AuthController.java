package com.krish.ticket_booking.controller;

import com.krish.ticket_booking.dto.request.LoginRequestDto;
import com.krish.ticket_booking.dto.request.UserRegisterRequest;
import com.krish.ticket_booking.dto.response.AuthResponse;
import com.krish.ticket_booking.entity.User;
import com.krish.ticket_booking.entity.enums.RoleEnum;
import com.krish.ticket_booking.exception.EmailAlreadyExistsException;
import com.krish.ticket_booking.repository.UserRepository;
import com.krish.ticket_booking.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping(path = "/register")
    public ResponseEntity<AuthResponse> register(@Valid  @RequestBody UserRegisterRequest request) {
        if(userRepository.findByEmail(request.email()).isPresent()){
            throw new EmailAlreadyExistsException("Email is already registered");
        }
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(RoleEnum.USER)
                .build();
        userRepository.save(user);

        Map<String, Object> claims = Map.of("role",user.getRole().name());
        String token = jwtService.generateToken(user.getEmail(),claims );

        return new ResponseEntity<AuthResponse>(new AuthResponse(token, user.getRole().name()), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequestDto request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        Map<String, Object> claims = Map.of("role",user.getRole().name());
        String token = jwtService.generateToken(user.getEmail(),claims );
        return new AuthResponse(token, user.getRole().name());
    }
    @GetMapping("/status")
    public String status() {
        return "Auth endpoint is working!";
    }
}
