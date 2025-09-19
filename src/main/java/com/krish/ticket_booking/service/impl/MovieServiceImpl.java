package com.krish.ticket_booking.service.impl;

import com.krish.ticket_booking.dto.request.MovieRegisterRequestDto;
import com.krish.ticket_booking.dto.response.MovieResponseDto;
import com.krish.ticket_booking.entity.Movie;
import com.krish.ticket_booking.exception.MovieNotFoundException;
import com.krish.ticket_booking.mapper.MovieMapper;
import com.krish.ticket_booking.repository.MovieRepository;
import com.krish.ticket_booking.repository.ScreenRepository;
import com.krish.ticket_booking.repository.TheaterRepository;
import com.krish.ticket_booking.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;
    private final MovieMapper movieMapper;

    @Override
    public MovieResponseDto CreateMovie(MovieRegisterRequestDto request) {
        Movie movie = Movie.builder()
                .title(request.title())
                .language(request.language())
                .posterUrl(request.posterUrl())
                .genre(request.genre())
                .duration(request.duration())
                .active(false)
                .build();

        Movie savedMovie = movieRepository.save(movie);
        return movieMapper.toDto(savedMovie);
    }

    @Override
    public List<MovieResponseDto> getListOfMovies(String location, UUID theaterId, UUID screenId) {
        Set<Movie> movies = new HashSet<>();

        if(screenId != null){
            screenRepository.findById(screenId).ifPresent(screen ->
                    screen.getShows().forEach(show -> movies.add(show.getMovie()))
            );

        } else if (theaterId != null) {

                theaterRepository.findById(theaterId).ifPresent(theater ->
                        theater.getScreens().forEach(screen ->
                                screen.getShows().forEach(show -> movies.add(show.getMovie()))
                        )
                );

        } else if(StringUtils.hasText(location)) {
               theaterRepository.findByLocationIgnoreCase(location).forEach(theater ->
                               theater.getScreens().forEach(screen->
                                       screen.getShows().forEach( show ->
                                               movies.add(show.getMovie())
                               )));
        } else {
            movies.addAll(movieRepository.findAll());
        }

        return movies.stream().map(movieMapper::toDto).toList();
    }

    @Override
    public MovieResponseDto findMovieById(UUID id) {
        return movieRepository.findById(id)
                .map(movieMapper::toDto)
                .orElseThrow(() -> new MovieNotFoundException("Movie not Found with id: " + id));
    }
}
