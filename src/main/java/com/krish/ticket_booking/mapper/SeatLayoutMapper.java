package com.krish.ticket_booking.mapper;

import com.krish.ticket_booking.dto.request.SeatLayoutRequestDto;
import com.krish.ticket_booking.dto.response.SeatLayoutResponseDto;
import com.krish.ticket_booking.entity.SeatLayout;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SeatLayoutMapper {
    SeatLayout toEntity(SeatLayoutRequestDto dto);

    SeatLayoutResponseDto toDto(SeatLayout entity);}
