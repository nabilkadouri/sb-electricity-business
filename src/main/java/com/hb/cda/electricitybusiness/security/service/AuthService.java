package com.hb.cda.electricitybusiness.security.service;

import com.hb.cda.electricitybusiness.controller.dto.auth.CodeCheckRequest;
import com.hb.cda.electricitybusiness.controller.dto.auth.LoginRequest;
import com.hb.cda.electricitybusiness.controller.dto.auth.LoginResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    void processLoginAndSendCode(LoginRequest loginRequest);

    LoginResponse verifyCodeAndGenerateTokens(CodeCheckRequest codeCheckRequest, HttpServletResponse response);

    void applyNewPassword(String token, String newPassword);

    void sendResetPasswordLink(String email);
}
