package com.hb.cda.electricitybusiness.business.impl;

import com.hb.cda.electricitybusiness.business.AccountBusiness;
import com.hb.cda.electricitybusiness.business.exception.BusinessException;
import com.hb.cda.electricitybusiness.business.exception.UserAlreadyExistsException;
import com.hb.cda.electricitybusiness.controller.dto.PictureDetailsDTO;
import com.hb.cda.electricitybusiness.controller.dto.UserEmailUpdateDto;
import com.hb.cda.electricitybusiness.messaging.MailService;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.repository.UserRepository;
import com.hb.cda.electricitybusiness.security.jwt.JwtUtil;
import com.hb.cda.electricitybusiness.service.UploadService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.util.List;

@Service
public class AccountBusinessImpl implements AccountBusiness {

    private final UploadService uploadService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private MailService mailService;

    public AccountBusinessImpl(UploadService uploadService, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, MailService mailService) {
        this.uploadService = uploadService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.mailService = mailService;
    }

    @Override
    @Transactional
    public User registerUser(User user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        String hashed = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashed);
        return userRepository.save(user);
    }


    @Override
    @Transactional
    public void updatePassword(Long userId, String oldPassword, String newPassword) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));

        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("Ancien mot de passe incorrect");
        }

        // Encoder le nouveau mot de passe
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }


    @Override
    public User getAuthenticatedUserResponse(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé avec l'email: " + email));
    }


    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));
    }


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));

        if (user.getProfilePicture() != null) {
            String currentSrc = user.getProfilePicture().getSrc();
            if (currentSrc != null && !currentSrc.startsWith("images/default_")) {
                uploadService.removeExisting(currentSrc);
            }
        }

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID: " + id);
        }
        userRepository.deleteById(id);
    }


    @Override
    @Transactional
    public User updateUserEmail(Long id, UserEmailUpdateDto emailUpdateDto) {
        // Vérifier si l'utilisateur existe
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));

        // Vérifier si l'email a réellement changé
        if (!existingUser.getEmail().equals(emailUpdateDto.getEmail())) {

            // Vérifier que le nouvel email n'est pas déjà utilisé
            if (userRepository.findByEmail(emailUpdateDto.getEmail()).isPresent()) {
                throw new IllegalArgumentException("L'email " + emailUpdateDto.getEmail() + " est déjà utilisé.");
            }

            // Mettre à jour l'email et sauvegarder
            existingUser.setEmail(emailUpdateDto.getEmail());
            return userRepository.save(existingUser);
        }

        // Si email identique → retourner l’utilisateur sans modification
        return existingUser;
    }



    @Override
    @Transactional
    public PictureDetailsDTO uploadProfilePicture(Long id, MultipartFile file, String altText, boolean isMain) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID " + id));

        // Supprimer l'ancienne photo sauf si c'est l'image par défaut
        if(user.getProfilePicture() != null
                && user.getProfilePicture().getSrc() != null
                && !user.getProfilePicture().getSrc().startsWith("images/default_")) {
            uploadService.removeExisting(user.getProfilePicture().getSrc());
        }

        // Upload nouvelle image
        String newFileName = uploadService.uploadImage(file);

        // Construire le bon chemin /uploads/...
        String finalSrc = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(newFileName)
                .toUriString();

        PictureDetailsDTO newPicture = new PictureDetailsDTO(
                altText != null ? altText : "Photo de profil",
                finalSrc,
                isMain
        );

        // 🔥 Mise à jour du user dans la base
        user.setProfilePicture(newPicture);
        userRepository.save(user);

        return newPicture;
    }

}
