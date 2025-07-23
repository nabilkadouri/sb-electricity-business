package com.hb.cda.electricitybusiness.service;

import com.hb.cda.electricitybusiness.dto.mapper.UserMapper;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserService {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UploadService uploadService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        //Initialiser les mocks avant chaque test
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserbYUsername_found() {
        String email = "test@postman.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.
    }


}
