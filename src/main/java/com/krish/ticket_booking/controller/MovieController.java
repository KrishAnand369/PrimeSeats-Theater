package com.krish.ticket_booking.controller;

import com.krish.ticket_booking.dto.request.MovieRegisterRequestDto;
import com.krish.ticket_booking.dto.response.MovieResponseDto;
import com.krish.ticket_booking.dto.response.TheaterResponseDto;
import com.krish.ticket_booking.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Movies")
public class MovieController {

    private  final MovieService movieService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponseDto> createMovie(@Valid@RequestBody MovieRegisterRequestDto movieRegisterRequestDto){
        try {
            MovieResponseDto movieResponseDto = movieService.CreateMovie(movieRegisterRequestDto);
            return new ResponseEntity<>(movieResponseDto, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            throw new RuntimeException("Movie not saved");
        }
    }

//    @PutMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<MovieResponseDto> editMovie(@Valid@RequestBody MovieRegisterRequestDto movieRegisterRequestDto){
//        try {
//            MovieResponseDto movieResponseDto = movieService.CreateMovie(movieRegisterRequestDto);
//            return new ResponseEntity<>(movieResponseDto, HttpStatus.OK);
//        } catch (RuntimeException e) {
//            throw new RuntimeException("Movie not saved");
//        }
//    }

    @GetMapping()
    public ResponseEntity<List<MovieResponseDto>> listMovies(
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "theater", required = false) UUID theater,
            @RequestParam(value = "screen", required = false) UUID screen

    ){
        return ResponseEntity.ok(movieService.getListOfMovies(location,theater,screen));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDto> movieInfoById(@PathVariable UUID id ){
        return ResponseEntity.ok(movieService.findMovieById(id));
    }
}
