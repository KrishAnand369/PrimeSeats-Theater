package com.krish.ticket_booking.mapper;

import com.krish.ticket_booking.dto.response.ShowSeatResponseDto;
import com.krish.ticket_booking.entity.ShowSeat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShowSeatMapper {
    @Mapping(target = "seatLabel", expression = "java(showSeat.getSeat().getSeatLabel())")
    @Mapping(target = "price", expression = "java(showSeat.getSeat().getPrice())")
    ShowSeatResponseDto toDto(ShowSeat showSeat);

}
