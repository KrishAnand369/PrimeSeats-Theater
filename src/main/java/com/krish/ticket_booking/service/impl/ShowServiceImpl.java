package com.krish.ticket_booking.service.impl;

import com.krish.ticket_booking.dto.request.ShowRegisterRequestDto;
import com.krish.ticket_booking.dto.response.ShowLayoutResponseDto;
import com.krish.ticket_booking.dto.response.ShowResponseDto;
import com.krish.ticket_booking.dto.response.ShowRowResponseDto;
import com.krish.ticket_booking.dto.response.ShowSeatResponseDto;
import com.krish.ticket_booking.entity.*;
import com.krish.ticket_booking.entity.enums.ShowSeatStatus;
import com.krish.ticket_booking.exception.MovieNotFoundException;
import com.krish.ticket_booking.exception.ScreenNotFoundException;
import com.krish.ticket_booking.exception.ShowNotFoundException;
import com.krish.ticket_booking.exception.UserNotFoundException;
import com.krish.ticket_booking.mapper.ShowMapper;
import com.krish.ticket_booking.repository.*;
import com.krish.ticket_booking.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowServiceImpl implements ShowService {

    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;
    private final TheaterRepository theaterRepository;
    private final ShowSeatRepository showSeatRepository;
    private final UserRepository userRepository;
    private final ShowMapper showMapper;

    @Transactional
    @Override
    public ShowResponseDto createShow(ShowRegisterRequestDto request,UUID screenId) {
        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new RuntimeException("Screen not found"));

        // Create show
        Show show = Show.builder()
                .movie(movie)
                .screen(screen)
                .startTime(request.startTime())
                .extraCharge(request.extraCharge() != null ? request.extraCharge() : 0.0)
                .build();

        // Build ShowSeats (seat + show + final price)
        List<ShowSeat> showSeats = new ArrayList<>();
       screen.getSeatLayout() // assuming one active layout for screen
        .getRows().forEach(row -> {
            row.getSeats().forEach(seat -> {
                ShowSeat ss = new ShowSeat();
                ss.setShow(show);
                seat.setPrice(seat.getPrice() + show.getExtraCharge());
                ss.setSeat(seat);
                ss.setStatus(ShowSeatStatus.AVAILABLE);
                showSeats.add(ss);
            });
        });


        show.setShowSeats(showSeats);

        Show saved = showRepository.save(show);

        return showMapper.toDto(saved);
    }

    @Override
    public ShowResponseDto getShow(UUID showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ShowNotFoundException("Show not found"));
        return showMapper.toDto(show);
    }

    @Override
    public ShowResponseDto updateShow(ShowRegisterRequestDto request,UUID screenId, UUID showId) {
        // 1. Fetch existing show
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ShowNotFoundException("Show not found"));

        // 2. Fetch referenced movie & screen
        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new MovieNotFoundException("Movie not found"));

        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new ScreenNotFoundException("Screen not found"));

        // 3. Update fields
        show.setMovie(movie);
        show.setScreen(screen);
        show.setStartTime(request.startTime());
        show.setExtraCharge(request.extraCharge() != null ? request.extraCharge() : 0.0);

        // 4. Update ShowSeats
        if (request.extraCharge() != null) {
            for (ShowSeat ss : show.getShowSeats()) {
                // adjust price based on new extraCharge
                double newPrice = ss.getSeat().getPrice() + show.getExtraCharge();
                ss.getSeat().setPrice(newPrice);
            }
        }

        // If screen changed → rebuild showSeats from that screen's layout
        if (!show.getScreen().getId().equals(screenId)) {
            List<ShowSeat> newSeats = new ArrayList<>();
            screen.getSeatLayout().getRows().forEach(row -> {
                row.getSeats().forEach(seat -> {
                    ShowSeat ss = new ShowSeat();
                    ss.setShow(show);
                    seat.setPrice(seat.getPrice() + show.getExtraCharge());
                    ss.setSeat(seat);
                    ss.setStatus(ShowSeatStatus.AVAILABLE);
                    newSeats.add(ss);
                });
            });
            show.getShowSeats().clear();
            show.getShowSeats().addAll(newSeats);
        }

        // 5. Save and return
        Show saved = showRepository.save(show);
        return showMapper.toDto(saved);
    }


    @Override
    public List<ShowResponseDto> getListOfShowsByManager(Authentication authentication) {
        List<Show> shows = new ArrayList<>();
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(()->new UserNotFoundException("Manager Not Found Exception"));
        List<Theater> theaters = theaterRepository.findByManagerId(user.getId());
        theaters.stream()
                .map(Theater::getScreens)
                .flatMap(List::stream)
                .map(Screen::getShows)
                .flatMap(List::stream)
                .forEach(shows::add);

        return shows.stream().map(showMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ShowLayoutResponseDto getShowLayout(UUID showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found"));

        // Group seats by row
        Map<String, List<ShowSeatResponseDto>> grouped = show.getShowSeats().stream()
                .map(ss -> new ShowSeatResponseDto(
                        ss.getId(),
                        ss.getSeat().getSeatLabel(),   // e.g., "A1"
                        ss.getStatus(),
                        ss.getSeat().getPrice() + show.getExtraCharge()
                ))
                .collect(Collectors.groupingBy(dto -> dto.seatLabel().substring(0,1)));

        // Map to rows
        List<ShowRowResponseDto> rows = grouped.entrySet().stream()
                .map(e -> new ShowRowResponseDto(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(ShowRowResponseDto::rowLabel))
                .toList();

        return new ShowLayoutResponseDto(
                show.getId(),
                show.getMovie().getTitle(),
                show.getStartTime(),
                rows
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> getShowBookedSeats(UUID showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found"));
        List<ShowSeat> bookedSeats = showSeatRepository.findByShowIdAndStatus(showId,ShowSeatStatus.BOOKED);
        return bookedSeats.stream().map(ShowSeat::getId).toList();
    }

    @Override
    public List<ShowResponseDto> listShowsByScreenId(UUID screenId) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new ScreenNotFoundException("Screen not found"));
        return showRepository.findByScreenId(screenId).stream().map(showMapper::toDto).toList();
    }
}
