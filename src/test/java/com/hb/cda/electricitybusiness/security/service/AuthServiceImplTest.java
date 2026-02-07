package com.hb.cda.electricitybusiness.security.service;

import com.hb.cda.electricitybusiness.controller.dto.auth.LoginRequest;
import com.hb.cda.electricitybusiness.messaging.MailService;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.repository.UserRepository;
import com.hb.cda.electricitybusiness.security.jwt.JwtUtil;
import com.hb.cda.electricitybusiness.security.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import com.hb.cda.electricitybusiness.controller.dto.auth.CodeCheckRequest;
import com.hb.cda.electricitybusiness.controller.dto.auth.LoginResponse;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletResponse;
import java.util.Optional;

class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MailService mailService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processLogin_ShouldGenerateVerificationCode() {
        // ARRANGE : préparation du contexte de test
        LoginRequest request =
                new LoginRequest("test@mail.com", "password");

        User user = new User();
        user.setEmail("test@mail.com");

        Authentication fakeAuth =
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                );

        when(authenticationManager.authenticate(any()))
                .thenReturn(fakeAuth);
        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        // ACT : appel de la méthode testée
        authService.processLoginAndSendCode(request);

        // ASSERT : vérification du comportement attendu
        assertNotNull(user.getCodeCheck());
        verify(userRepository, times(1)).save(user);
        verify(mailService, times(1))
                .sendVerificationCode(eq("test@mail.com"), anyString());
    }


    @Test
    void verifyCode_ShouldReturnAccessToken_WhenCodeIsValid() {
        // ARRANGE
        User user = new User();
        user.setEmail("test@mail.com");
        user.setCodeCheck("123456");

        CodeCheckRequest dto = new CodeCheckRequest("test@mail.com", "123456");

        when(userRepository.findByEmailAndCodeCheck("test@mail.com", "123456"))
                .thenReturn(Optional.of(user));

        when(jwtUtil.generateToken(user)).thenReturn("ACCESS_TOKEN");
        when(jwtUtil.generateRefreshToken(user)).thenReturn("REFRESH_TOKEN");

        MockHttpServletResponse response = new MockHttpServletResponse();

        // ACT
        LoginResponse login = authService.verifyCodeAndGenerateTokens(dto, response);

        // ASSERT
        assertEquals("ACCESS_TOKEN", login.getAccessToken());
        assertTrue(response.getCookies().length > 0);
        assertNull(user.getCodeCheck());
        verify(userRepository).save(user);
    }
}
