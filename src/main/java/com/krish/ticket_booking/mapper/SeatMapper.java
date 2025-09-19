package com.krish.ticket_booking.mapper;

import com.krish.ticket_booking.dto.request.SeatRequestDto;
import com.krish.ticket_booking.dto.response.SeatResponseDto;
import com.krish.ticket_booking.entity.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SeatMapper {

    //@Mapping(target = "seatLabel",expression = "java(seat.getSeatRow().getRowLabel() + seat.getSeatNumber())")-> avoided because we used @Transient method in seat
    SeatResponseDto toDto(Seat seat);

    //@Mapping(target = "category",)
    Seat toEntity(SeatRequestDto seatRequestDto);
}
