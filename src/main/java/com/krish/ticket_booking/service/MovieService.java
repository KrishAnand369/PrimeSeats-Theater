package com.krish.ticket_booking.service;

import com.krish.ticket_booking.dto.request.MovieRegisterRequestDto;
import com.krish.ticket_booking.dto.response.MovieResponseDto;

import java.util.List;
import java.util.UUID;

public interface MovieService {

    MovieResponseDto CreateMovie(MovieRegisterRequestDto movieRegisterRequestDto);

    List<MovieResponseDto> getListOfMovies(String location, UUID theater, UUID screen);

    MovieResponseDto findMovieById(UUID id);
}
