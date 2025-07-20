package com.hb.cda.electricitybusiness.controller;

import com.hb.cda.electricitybusiness.dto.PictureDetailsDTO;
import com.hb.cda.electricitybusiness.dto.UserEmailUpdateDto;
import com.hb.cda.electricitybusiness.dto.UserResponse;
import com.hb.cda.electricitybusiness.dto.UserUpdateRequest;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.repository.UserRepository;
import com.hb.cda.electricitybusiness.security.dto.auth.RegisterRequest;
import com.hb.cda.electricitybusiness.service.UploadService;
import com.hb.cda.electricitybusiness.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserRepository userRepository;
    private UserService userService;
    private UploadService uploadService;

    public UserController(UserRepository userRepository, UserService userService, UploadService uploadService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.uploadService = uploadService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody RegisterRequest request) {
        User newUser = userService.registerUser(request);
        UserResponse userResponse = userService.getUserResponse(newUser);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @PostMapping("/{userId}/uploadProfilePicture")
    public ResponseEntity<String> uploadProfilePicture(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file, // Le fichier envoyé dans la requête
            @RequestParam(value = "alt", required = false) String altText, // Texte alternatif (optionnel)
            @RequestParam(value = "isMain", defaultValue = "true") boolean isMain // Image principale (par défaut true)
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID " + userId));

        // LOGIQUE DE SUPPRESSION DE L'ANCIENNE IMAGE :
        // Vérifie si l'utilisateur avait déjà une image (qui n'est pas une image par défaut)
        if (user.getProfilePicture() != null) {
            String currentSrc = user.getProfilePicture().getSrc();
            // Si l'image n'est pas une image par défaut (qui sont stockées dans /static/images/)
            if (currentSrc != null && !currentSrc.startsWith("images/default_")) {
                uploadService.removeExisting(currentSrc); // Supprime l'ancienne image et sa miniature
            }
        }

        // LOGIQUE D'UPLOAD DE LA NOUVELLE IMAGE :
        // Appelle le service d'upload pour sauvegarder la nouvelle image
        String newFileName = uploadService.uploadImage(file); // Retourne le nom de fichier unique

        // MISE À JOUR DE L'ENTITÉ USER :
        // Crée un nouvel objet PictureDetailsDTO avec les infos de la nouvelle image
        // Note : le 'src' stocke le NOM DU FICHIER UNIQUE, pas l'URL complète
        PictureDetailsDTO newPictureDetails = new PictureDetailsDTO(altText, newFileName, isMain);
        user.setProfilePicture(newPictureDetails);
        userRepository.save(user); // Sauvegarde l'utilisateur mis à jour dans la base de données

        // CONSTRUCTION DE L'URL POUR LA RÉPONSE :
        // Crée l'URL complète pour que le client sache où accéder à la nouvelle image
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/") // Chemin exposé par WebConfig
                .path(newFileName) // Le nom du fichier
                .toUriString();

        return ResponseEntity.ok("Photo de profil uploadée avec succès pour l'utilisateur " + userId + ". URL: " + fileDownloadUri);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        UserResponse updatedUser = userService.updateUser(id, request);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PatchMapping("/{id}/email")
    public ResponseEntity<UserResponse> updateUserEmail(@PathVariable Long id, @Valid @RequestBody UserEmailUpdateDto emailUpdateDto) {
        UserResponse updatedUser = userService.updateUserEmail(id, emailUpdateDto);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
