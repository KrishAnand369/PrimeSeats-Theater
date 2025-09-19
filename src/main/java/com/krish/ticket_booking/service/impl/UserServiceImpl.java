package com.krish.ticket_booking.service.impl;

import com.krish.ticket_booking.dto.request.StaffRegisterRequest;
import com.krish.ticket_booking.entity.User;
import com.krish.ticket_booking.exception.EmailAlreadyExistsException;
import com.krish.ticket_booking.repository.UserRepository;
import com.krish.ticket_booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private  final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUserWithRole(StaffRegisterRequest request) {
        if(userRepository.findByEmail(request.email()).isPresent()){
            throw new EmailAlreadyExistsException("Email is already registered");
        }
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();
        return userRepository.save(user);
    }
}
