package com.hb.cda.electricitybusiness.service;

import com.hb.cda.electricitybusiness.dto.PictureDetailsDTO;
import com.hb.cda.electricitybusiness.dto.UserEmailUpdateDto;
import com.hb.cda.electricitybusiness.dto.UserResponse;
import com.hb.cda.electricitybusiness.dto.UserUpdateRequest;
import com.hb.cda.electricitybusiness.dto.mapper.UserMapper;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.repository.UserRepository;
import com.hb.cda.electricitybusiness.security.dto.auth.RegisterRequest;

import jakarta.transaction.Transactional;
import org.hibernate.sql.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService  implements UserDetailsService {
    private final UploadService uploadService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, UploadService uploadService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.uploadService = uploadService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec email: " + email));
    }

    public UserResponse getAuthenticatedUserResponse(String email) {
        User user = (User) loadUserByUsername(email);
        return userMapper.userToUserResponse(user);
    }



    @Transactional
    public User registerUser(RegisterRequest dto) {
        Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());

        if(existingUser.isPresent()) {
            throw new IllegalArgumentException("L'utilisateur avec l'email: " + dto.getEmail() + "existe déjà.");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setFirstName(dto.getFirstName());
        user.setAddress(dto.getAddress());
        user.setPostaleCode(dto.getPostaleCode());
        user.setCity(dto.getCity());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setOwnsStation(false);
        user.setCodeCheck(null);
        user.setLatitude(dto.getLatitude());
        user.setLongitude(dto.getLongitude());

        return userRepository.save(user);
    }

    public UserResponse getUserResponse(User user){
        return userMapper.userToUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::userToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));
        return userMapper.userToUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest updateRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));

        if (updateRequest.getEmail() != null && !existingUser.getEmail().equals(updateRequest.getEmail())) {
            if (userRepository.findByEmail(updateRequest.getEmail()).isPresent()) {
                throw new IllegalArgumentException("L'email " + updateRequest.getEmail() + " est déjà utilisé.");
            }
            existingUser.setEmail(updateRequest.getEmail());
        }

        userMapper.updateUserFromRequest(updateRequest, existingUser);

        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }

        User updateUser =userRepository.save(existingUser);
        return userMapper.userToUserResponse(updateUser);
    }

    @Transactional
    public UserResponse updateUserEmail(Long id, UserEmailUpdateDto emailUpdateDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));

        // Vérifier si le nouvel email est déjà utilisé par un autre utilisateur
        if (!existingUser.getEmail().equals(emailUpdateDto.getEmail())) {
            if (userRepository.findByEmail(emailUpdateDto.getEmail()).isPresent()) {
                throw new IllegalArgumentException("L'email " + emailUpdateDto.getEmail() + " est déjà utilisé.");
            }
            existingUser.setEmail(emailUpdateDto.getEmail());
        } else {
            return userMapper.userToUserResponse(existingUser);
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.userToUserResponse(updatedUser);
    }

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

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));

        if (user.getProfilePicture() != null) {
            String currentSrc = user.getProfilePicture().getSrc();
            // Vérifie que l'image à supprimer n'est pas une image par défaut
            if (currentSrc != null && !currentSrc.startsWith("images/default_")) {
                uploadService.removeExisting(currentSrc);
            }
        }

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID: " + id);
        }
        userRepository.deleteById(id);

    }

}
