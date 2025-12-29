package com.hb.cda.electricitybusiness.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hb.cda.electricitybusiness.business.AccountBusiness;
import com.hb.cda.electricitybusiness.controller.dto.RegisterRequest;
import com.hb.cda.electricitybusiness.controller.dto.UserResponse;
import com.hb.cda.electricitybusiness.controller.dto.mapper.UserMapper;
import com.hb.cda.electricitybusiness.messaging.MailService;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.security.jwt.JwtUtil;
import com.hb.cda.electricitybusiness.service.UploadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private UploadService uploadService;
    @MockBean private MailService mailService;
    @MockBean private JwtUtil jwtUtil;
    @MockBean private AccountBusiness accountBusiness;
    @MockBean private UserMapper userMapper;

    @Test
    void registerUser_shouldCreateUser_andReturn201() throws Exception {

        RegisterRequest request = new RegisterRequest(
                "john.doe@test.com",
                "Password123!",
                "Doe",
                "John",
                "10 rue de Paris",
                "75000",
                "Paris",
                48.85,
                2.35,
                "0600000000"
        );

        User created = new User();
        created.setId(1L);
        created.setEmail("john.doe@test.com");

        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setEmail("john.doe@test.com");

        when(accountBusiness.registerUser(any())).thenReturn(created);
        when(userMapper.userToUserResponse(created)).thenReturn(response);

        mockMvc.perform(post("/api/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("john.doe@test.com"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getUserById_ShouldReturnUserResponse() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");

        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setEmail("test@mail.com");

        when(accountBusiness.getUserById(1L)).thenReturn(user);
        when(userMapper.userToUserResponse(user)).thenReturn(response);

        mockMvc.perform(get("/api/account/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@mail.com"));
    }
}
