package com.krish.ticket_booking.mapper;

import com.krish.ticket_booking.dto.request.SeatRowRequestDto;
import com.krish.ticket_booking.dto.response.SeatRowResponseDto;
import com.krish.ticket_booking.entity.SeatRow;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE,uses = SeatMapper.class)
public interface SeatRowMapper {
    SeatRowResponseDto toDto(SeatRow seatRow);

    SeatRow toEntity(SeatRowRequestDto seatRowRequestDto);
}
