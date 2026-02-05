package com.hb.cda.electricitybusiness.security.service.impl;

import com.hb.cda.electricitybusiness.business.exception.BusinessException;
import com.hb.cda.electricitybusiness.controller.dto.auth.CodeCheckRequest;
import com.hb.cda.electricitybusiness.controller.dto.auth.LoginRequest;
import com.hb.cda.electricitybusiness.controller.dto.auth.LoginResponse;
import com.hb.cda.electricitybusiness.messaging.MailService;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.repository.UserRepository;
import com.hb.cda.electricitybusiness.security.jwt.JwtUtil;
import com.hb.cda.electricitybusiness.security.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private UserRepository userRepository;
    private MailService mailService;
    private PasswordEncoder passwordEncoder;
    @Value("${app.cookie.secure}")
    private boolean secureCookie;


    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, MailService mailService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional
    public void processLoginAndSendCode(LoginRequest loginRequest) {

       // Vérifier les identifiants (email / mot de passe)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // Récupérer l'utilisateur après validation des identifiants
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé après authentification réussie."));

        // Générer et stocker le code
        String codeVerification = generateCode();
        user.setCodeCheck(codeVerification);
        userRepository.save(user);

        // Envoyer code par email
        mailService.sendVerificationCode(user.getEmail(),codeVerification);
    }


    @Override
    @Transactional
    public LoginResponse verifyCodeAndGenerateTokens(CodeCheckRequest codeCheckRequest, HttpServletResponse response) {
        User user = userRepository.findByEmailAndCodeCheck(codeCheckRequest.getEmail(), codeCheckRequest.getCodeCheck())
                .orElseThrow(() -> new RuntimeException("Code de vérification invalide ou expiré pour l'email: " + codeCheckRequest.getEmail()));

        // Effacer le code de vérification
        user.setCodeCheck(null);
        userRepository.save(user);

        // Générer les tokens
        UserDetails userDetails = user;
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // Cookie refresh token (compatible navigateur)
        response.addHeader(
                "Set-Cookie",
                "refreshToken=" + refreshToken
                        + "; Path=/"
                        + "; HttpOnly"
                        + "; Secure"
                        + "; SameSite=None"
                        + "; Max-Age=" + (jwtUtil.getRefreshExpirationTime() / 1000)
        );

        return new LoginResponse(accessToken);
    }


    private String generateCode() {
        Random random = new Random();
        int code = random.nextInt(1_000_000);
        return String.format("%06d", code);
    }


    @Override
    @Transactional
    public void applyNewPassword(String token, String newPassword) {

        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void sendResetPasswordLink(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Email introuvable"));

        String token = jwtUtil.generateToken(user);

        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        mailService.sendResetPassword(user, resetLink);
    }

}
