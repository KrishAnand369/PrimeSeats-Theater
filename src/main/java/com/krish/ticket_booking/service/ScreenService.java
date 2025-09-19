package com.krish.ticket_booking.service;

import com.krish.ticket_booking.dto.request.ScreenRegisterRequest;
import com.krish.ticket_booking.dto.response.ScreenResponse;

import java.util.List;
import java.util.UUID;

public interface ScreenService {

    ScreenResponse createScreen(ScreenRegisterRequest request,UUID theaterId);

    List<ScreenResponse> listScreenByTheaterId(UUID theaterId);
}
