package com.krish.ticket_booking.service.impl;

import com.krish.ticket_booking.dto.request.TheaterRegisterRequest;
import com.krish.ticket_booking.dto.response.TheaterResponseDto;
import com.krish.ticket_booking.entity.Theater;
import com.krish.ticket_booking.entity.User;
import com.krish.ticket_booking.entity.enums.RoleEnum;
import com.krish.ticket_booking.exception.TheaterNotFoundException;
import com.krish.ticket_booking.exception.UserNotFoundException;
import com.krish.ticket_booking.mapper.TheaterMapper;
import com.krish.ticket_booking.repository.TheaterRepository;
import com.krish.ticket_booking.repository.UserRepository;
import com.krish.ticket_booking.service.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TheaterServiceImpl implements TheaterService {

    private final UserRepository userRepository;
    private final TheaterRepository theaterRepository;
    private final TheaterMapper theaterMapper;

    @Override
    public TheaterResponseDto createTheater(TheaterRegisterRequest dto) {

        var manager = userRepository.findById(dto.managerId())
                .orElseThrow(() -> new UserNotFoundException("Manager not found"));

        if (!manager.getRole().equals(RoleEnum.MANAGER)) {
            throw new RuntimeException("Assigned user is not a manager");
        }

        try {
            Theater theater = Theater.builder()
                    .name(dto.name())
                    .location(dto.location())
                    .manager(manager)
                    .build();

            Theater saved = theaterRepository.save(theater);
            return theaterMapper.toDto(saved);
        } catch (Exception e) {
            throw new RuntimeException("Error in Creating Theater");
        }
    }

    @Override
    public List<TheaterResponseDto> getListOfTheaters(String location) {
        List<Theater> theaterList = new ArrayList<>();
        if(StringUtils.hasText(location)){
           theaterList =  theaterRepository.findByLocationIgnoreCase(location);
        }  else {
            theaterList = theaterRepository.findAll();
        }
        return theaterList.stream().map(theaterMapper::toDto).toList();
    }

    @Override
    public void assignTheaterManager(UUID theaterId, UUID userId) {
        User manager = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not Found with id: " + userId));
        Theater theater = theaterRepository.findById(theaterId).orElseThrow(()-> new TheaterNotFoundException("Theater not found with id: "+theaterId));
        if (!manager.getRole().equals(RoleEnum.MANAGER)) {
            throw new RuntimeException("Assigned user is not a manager");
        }
        theater.setManager(manager);
    }

    @Override
    public List<TheaterResponseDto> getListOfTheatersByManager(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(()->new UserNotFoundException("Manager Not Found Exception"));
        List<Theater> theaters = theaterRepository.findByManagerId(user.getId());
        return theaters.stream().map(theaterMapper::toDto).toList();
    }

}
