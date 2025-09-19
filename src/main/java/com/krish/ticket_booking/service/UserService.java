package com.krish.ticket_booking.service;

import com.krish.ticket_booking.dto.request.StaffRegisterRequest;
import com.krish.ticket_booking.entity.User;

public interface UserService {

    public User createUserWithRole(StaffRegisterRequest req);
}
