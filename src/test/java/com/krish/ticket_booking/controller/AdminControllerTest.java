package com.krish.ticket_booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krish.ticket_booking.dto.request.StaffRegisterRequest;
import com.krish.ticket_booking.entity.enums.RoleEnum;
import com.krish.ticket_booking.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createUser_ShouldCreateUser_Successfully() throws Exception {
        // Arrange
        StaffRegisterRequest request = new StaffRegisterRequest(
                "John Staff",
                "staff@example.com",
                "password123",
                RoleEnum.MANAGER
        );

        doNothing().when(userService).createUserWithRole(any(StaffRegisterRequest.class));

        // Act & Assert
        mockMvc.perform(post("/api/admin/staffs")
                        .with(csrf()) // Required for non-GET requests in tests with Security config
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(userService).createUserWithRole(any(StaffRegisterRequest.class));
    }
}