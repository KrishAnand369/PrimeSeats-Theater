package com.krish.ticket_booking.mapper;

import com.krish.ticket_booking.dto.response.ShowResponseDto;
import com.krish.ticket_booking.entity.Show;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShowMapper {

    @Mapping(target = "movieId", expression = "java(show.getMovie().getId())")
    @Mapping(target = "screenId", expression = "java(show.getScreen().getId())")
    ShowResponseDto toDto(Show show);
}

