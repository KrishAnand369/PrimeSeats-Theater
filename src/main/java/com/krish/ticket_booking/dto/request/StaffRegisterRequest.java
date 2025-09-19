package com.krish.ticket_booking.dto.request;

import com.krish.ticket_booking.entity.enums.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StaffRegisterRequest( @NotBlank String name,
        @Email String email,
        @NotBlank String password,
        @NotNull RoleEnum role) {
}
