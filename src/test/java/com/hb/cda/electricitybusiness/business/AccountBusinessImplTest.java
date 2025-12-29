package com.hb.cda.electricitybusiness.business;

import com.hb.cda.electricitybusiness.business.exception.UserAlreadyExistsException;
import com.hb.cda.electricitybusiness.business.impl.AccountBusinessImpl;
import com.hb.cda.electricitybusiness.controller.dto.UserEmailUpdateDto;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.repository.UserRepository;
import com.hb.cda.electricitybusiness.security.jwt.JwtUtil;
import com.hb.cda.electricitybusiness.service.UploadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountBusinessImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UploadService uploadService;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private AccountBusinessImpl accountBusiness;

    @Test
    void registerUser_ShouldCreateUser_WhenEmailNotExists() {

        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("azerty");

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("azerty")).thenReturn("HASHED_PASSWORD");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = accountBusiness.registerUser(user);

        assertEquals("HASHED_PASSWORD", result.getPassword());
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailExists() {
        User user = new User();
        user.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> accountBusiness.registerUser(user));
    }

    @Test
    void updateUserEmail_ShouldUpdateSuccessfully() {
        User user = new User();
        user.setId(1L);
        user.setEmail("old@mail.com");

        UserEmailUpdateDto dto = new UserEmailUpdateDto("new@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(user);

        User result = accountBusiness.updateUserEmail(1L, dto);

        assertEquals("new@mail.com", result.getEmail());
    }

    @Test
    void updateUserEmail_ShouldThrowException_WhenEmailAlreadyExists() {
        User user = new User();
        user.setId(1L);
        user.setEmail("old@mail.com");

        UserEmailUpdateDto dto = new UserEmailUpdateDto("existing@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("existing@mail.com")).thenReturn(Optional.of(new User()));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountBusiness.updateUserEmail(1L, dto)
        );

        assertEquals("L'email existing@mail.com est déjà utilisé.", exception.getMessage());
    }
}
