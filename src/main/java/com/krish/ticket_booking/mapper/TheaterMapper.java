package com.krish.ticket_booking.mapper;

import com.krish.ticket_booking.dto.request.TheaterRegisterRequest;
import com.krish.ticket_booking.dto.response.TheaterResponseDto;
import com.krish.ticket_booking.entity.Theater;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TheaterMapper {

    TheaterResponseDto toDto(Theater theater);

    Theater toEntity(TheaterRegisterRequest dto);
}
