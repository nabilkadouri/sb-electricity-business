package com.hb.cda.electricitybusiness.controller;

import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.repository.UserRepository;
import com.hb.cda.electricitybusiness.security.JwtUtil;
import com.hb.cda.electricitybusiness.security.dto.auth.CodeCheckRequest;
import com.hb.cda.electricitybusiness.security.dto.auth.LoginRequest;
import com.hb.cda.electricitybusiness.security.dto.auth.LoginResponse;
import com.hb.cda.electricitybusiness.service.util.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private UserRepository userRepository;
    private EmailService emailService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }


    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser (@Valid @RequestBody LoginRequest loginRequest) {
        try {
            //Tenter l'authentification
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            //Si l'authentification réussit, récuperer l'entité user
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé après authentification réussie."));

            //Générer un code a 6 chiffres, en utilisant la methode generateCode
            String codeVerification = generateCode();
            //Ajouter code dans la propriété codeCheck du user
            user.setCodeCheck(codeVerification);
            userRepository.save(user);
            //Envoyer le code de verification par email
            emailService.sendVerificationCode(user.getEmail(), codeVerification);

            // Informer le client de la prochaine étape.
            return ResponseEntity.ok("Code de vérification envoyé à votre email. Veuillez le vérifier sur /api/auth/login/check");

        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Identifiants invalides", HttpStatus.UNAUTHORIZED);
        }

    }

    private String generateCode() {
        Random random = new Random();
        int code = random.nextInt(1_000_000);
        return String.format("%06d", code);
    }

    @PostMapping("/login/check")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody CodeCheckRequest codeCheckRequest) {
        try {
            User user = userRepository.findByEmailAndCodeCheck(codeCheckRequest.getEmail(), codeCheckRequest.getCodeCheck())
                    .orElseThrow(() -> new RuntimeException("Code de vérification invalide ou expiré pour l'email: " + codeCheckRequest.getEmail()));
            //Si le code est valide, l'effacer de la BDD
            System.out.println("Utilisateur trouvé et code validé pour : " + user.getEmail() + user.getCodeCheck()) ;

            user.setCodeCheck(null);
            userRepository.save(user);

            //Créer un userdetails (possible de le faire direct car user implement UserDetail)
            UserDetails userDetails = user;
            //Générer un token
            String accessToken = jwtUtil.generateToken(userDetails);
            //Générer un refresh token
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            //Retourner les tokens dans la reponse
            return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));

        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Erreur lors de la génération du token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
