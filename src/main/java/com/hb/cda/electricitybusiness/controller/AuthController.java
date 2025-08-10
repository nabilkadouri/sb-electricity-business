package com.hb.cda.electricitybusiness.controller;

import com.hb.cda.electricitybusiness.controller.dto.auth.CodeCheckRequest;
import com.hb.cda.electricitybusiness.controller.dto.auth.LoginRequest;
import com.hb.cda.electricitybusiness.controller.dto.auth.LoginResponse;
import com.hb.cda.electricitybusiness.controller.dto.auth.MessageResponse;
import com.hb.cda.electricitybusiness.security.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

   private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<MessageResponse> authenticateUser (@Valid @RequestBody LoginRequest loginRequest) {
        try {
            authService.processLoginAndSendCode(loginRequest);
            return ResponseEntity.ok(new MessageResponse("Code de vérification envoyé à votre email. Veuillez le vérifier sur /api/auth/login/check"));
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(new MessageResponse("Identifiants invalides"), HttpStatus.UNAUTHORIZED);
        }

    }

    @PostMapping("/login/check")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody CodeCheckRequest codeCheckRequest, HttpServletResponse response) {
        try {
            LoginResponse loginResponse = authService.verifyCodeAndGenerateTokens(codeCheckRequest, response);
            return ResponseEntity.ok(loginResponse);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
