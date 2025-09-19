package com.krish.ticket_booking.mapper;

import com.krish.ticket_booking.dto.request.ScreenRegisterRequest;
import com.krish.ticket_booking.dto.response.ScreenResponse;
import com.krish.ticket_booking.entity.Screen;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScreenMapper {

    @Mapping(target = "theaterId",expression = "java(screen.getTheater().getId())")
    ScreenResponse toDto(Screen screen);

    Screen toEntity(ScreenRegisterRequest dto);
}
