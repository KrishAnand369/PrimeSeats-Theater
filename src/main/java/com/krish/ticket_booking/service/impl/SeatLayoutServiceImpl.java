package com.krish.ticket_booking.service.impl;

import com.krish.ticket_booking.dto.request.SeatLayoutRequestDto;
import com.krish.ticket_booking.dto.request.SeatRequestDto;
import com.krish.ticket_booking.dto.request.SeatRowRequestDto;
import com.krish.ticket_booking.dto.response.SeatLayoutResponseDto;
import com.krish.ticket_booking.entity.Screen;
import com.krish.ticket_booking.entity.Seat;
import com.krish.ticket_booking.entity.SeatLayout;
import com.krish.ticket_booking.entity.SeatRow;
import com.krish.ticket_booking.mapper.SeatLayoutMapper;
import com.krish.ticket_booking.mapper.SeatMapper;
import com.krish.ticket_booking.mapper.SeatRowMapper;
import com.krish.ticket_booking.repository.ScreenRepository;
import com.krish.ticket_booking.repository.SeatLayoutRepository;
import com.krish.ticket_booking.repository.SeatRepository;
import com.krish.ticket_booking.repository.SeatRowRepository;
import com.krish.ticket_booking.service.SeatLayoutService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SeatLayoutServiceImpl implements SeatLayoutService {

    private final ScreenRepository screenRepository;
    private final SeatLayoutRepository layoutRepository;
    private final SeatLayoutValidator seatLayoutValidator;
    //private final SeatRepository seatRepository;
    private final SeatLayoutMapper layoutMapper;
    private final SeatRowMapper rowMapper;
    private final SeatMapper seatMapper;

    @Transactional
    @Override
    public SeatLayoutResponseDto createLayout(UUID screenId, SeatLayoutRequestDto request) {
        // 1. Fetch parent screen
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new RuntimeException("Screen not found"));

        // 2. Validate request
        seatLayoutValidator.validateLayout(request, screen);

        // 3. Map layout DTO → entity
        SeatLayout layout = layoutMapper.toEntity(request);
        layout.setScreen(screen);

        // 4. Map rows & seats
        List<SeatRow> rows = new ArrayList<>();
        for (SeatRowRequestDto rowDto : request.rows()) {
            SeatRow row = rowMapper.toEntity(rowDto);
            row.setSeatLayout(layout);

            List<Seat> seats = new ArrayList<>();
            for (SeatRequestDto seatDto : rowDto.seats()) {
                Seat seat = seatMapper.toEntity(seatDto);
                seat.setSeatRow(row);
                seats.add(seat);
            }
            row.setSeats(seats);
            rows.add(row);
        }
        layout.setRows(rows);

        // 4. Save layout (cascade saves rows + seats)
        SeatLayout saved = layoutRepository.save(layout);

        return layoutMapper.toDto(saved);
    }

    @Override
    public SeatLayoutResponseDto getLayout(UUID layoutId) {
        SeatLayout layout = layoutRepository.findById(layoutId)
                .orElseThrow(() -> new RuntimeException("Layout not found"));
        return layoutMapper.toDto(layout);
    }

    @Override
    public List<SeatLayoutResponseDto> listLayoutsByScreen(UUID screenId) {
        List<SeatLayout> layouts = layoutRepository.findByScreenId(screenId);
        return layouts.stream().map(layoutMapper::toDto).toList();
    }

    @Transactional
    @Override
    public SeatLayoutResponseDto updateLayout(UUID layoutId, SeatLayoutRequestDto request) {
        SeatLayout layout = layoutRepository.findById(layoutId)
                .orElseThrow(() -> new RuntimeException("Layout not found"));

        seatLayoutValidator.validateLayout(request, layout.getScreen());

        // Update top-level fields
        layout.setLayoutType(request.layoutType());

        // Remove existing rows (cascade will delete seats too)
        layout.getRows().clear();

        // Add new rows + seats
        List<SeatRow> newRows = new ArrayList<>();
        for (SeatRowRequestDto rowDto : request.rows()) {
            SeatRow row = rowMapper.toEntity(rowDto);
            row.setSeatLayout(layout);

            List<Seat> newSeats = new ArrayList<>();
            for (SeatRequestDto seatDto : rowDto.seats()) {
                Seat seat = seatMapper.toEntity(seatDto);
                seat.setSeatRow(row);
                newSeats.add(seat);
            }
            row.setSeats(newSeats);
            newRows.add(row);
        }
        layout.setRows(newRows);

        SeatLayout saved = layoutRepository.save(layout);
        return layoutMapper.toDto(saved);
    }


    @Transactional
    @Override
    public void deleteLayout(UUID layoutId) {
        if (!layoutRepository.existsById(layoutId)) {
            throw new RuntimeException("Layout not found");
        }
        layoutRepository.deleteById(layoutId);
    }
}
