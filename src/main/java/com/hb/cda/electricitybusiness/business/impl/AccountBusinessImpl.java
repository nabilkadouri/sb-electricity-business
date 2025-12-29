package com.hb.cda.electricitybusiness.business.impl;

import com.hb.cda.electricitybusiness.business.AccountBusiness;
import com.hb.cda.electricitybusiness.business.exception.BusinessException;
import com.hb.cda.electricitybusiness.business.exception.UserAlreadyExistsException;
import com.hb.cda.electricitybusiness.controller.dto.PictureDetailsDTO;
import com.hb.cda.electricitybusiness.controller.dto.UserEmailUpdateDto;
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

    public AccountBusinessImpl(UploadService uploadService, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.uploadService = uploadService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
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
    public void resetPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Email non trouvé"));
        UserDetails userDetails = user;
        String token = jwtUtil.generateToken(userDetails);
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
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));

        if (!existingUser.getEmail().equals(emailUpdateDto.getEmail())) {
            if (userRepository.findByEmail(emailUpdateDto.getEmail()).isPresent()) {
                throw new IllegalArgumentException("L'email " + emailUpdateDto.getEmail() + " est déjà utilisé.");
            }
            existingUser.setEmail(emailUpdateDto.getEmail());
            return userRepository.save(existingUser);
        }
        return existingUser;
    }

    @Override
    @Transactional
    public String uploadProfilePicture(Long id, MultipartFile file, String altText, boolean isMain) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID " + id));

        //Supprimer ancienne image, sauf si c'est celle par défaut
        if(user.getProfilePicture() != null && !user.getProfilePicture().getSrc().startsWith("images/default_")) {
            String currentSrc = user.getProfilePicture().getSrc();
            uploadService.removeExisting(currentSrc);
        }

        //Upload de la nouvelle image
        String newFileName = uploadService.uploadImage(file);

        //Mise à jour de l'entité user
        PictureDetailsDTO newPictureDetails = new PictureDetailsDTO(altText, newFileName, isMain);
        user.setProfilePicture(newPictureDetails);
        userRepository.save(user);

        // Construction de l'url pour la réponse
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(newFileName)
                .toUriString();

        return fileDownloadUri;
    }
}
