package com.krish.ticket_booking.service.impl;

import com.krish.ticket_booking.dto.request.ScreenRegisterRequest;
import com.krish.ticket_booking.dto.response.ScreenResponse;
import com.krish.ticket_booking.entity.Screen;
import com.krish.ticket_booking.entity.Theater;
import com.krish.ticket_booking.exception.TheaterNotFoundException;
import com.krish.ticket_booking.mapper.ScreenMapper;
import com.krish.ticket_booking.repository.ScreenRepository;
import com.krish.ticket_booking.repository.TheaterRepository;
import com.krish.ticket_booking.service.ScreenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScreenServiceImpl implements ScreenService {

    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;
    private final ScreenMapper screenMapper;


    @Override
    public ScreenResponse createScreen(ScreenRegisterRequest request,UUID theaterId) {
        Theater theater = theaterRepository.findById(theaterId).orElseThrow(() -> new TheaterNotFoundException("Theater not found with id: " + theaterId));
        Screen screen = Screen.builder().name(request.name()).totalSeats(request.totalSeats()).theater(theater).build();

        Screen savedScreen = screenRepository.save(screen);
        return screenMapper.toDto(savedScreen);
    }

    @Override
    public List<ScreenResponse> listScreenByTheaterId(UUID theaterId) {
        Theater theater = theaterRepository.findById(theaterId).orElseThrow(() -> new TheaterNotFoundException("Theater not found with id: " + theaterId));
        List<Screen> screens = new ArrayList<>();
        screens = screenRepository.findByTheaterId(theaterId);
        return screens.stream().map(screenMapper::toDto).toList();

    }
}
