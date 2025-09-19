package com.krish.ticket_booking.mapper;

import com.krish.ticket_booking.dto.request.MovieRegisterRequestDto;
import com.krish.ticket_booking.dto.response.MovieResponseDto;
import com.krish.ticket_booking.entity.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MovieMapper {
    MovieResponseDto toDto(Movie movie);

    Movie toEntity(MovieRegisterRequestDto movieRegisterRequestDto);
}
