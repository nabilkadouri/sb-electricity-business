package com.hb.cda.electricitybusiness.controller;

import com.hb.cda.electricitybusiness.business.AccountBusiness;
import com.hb.cda.electricitybusiness.controller.dto.*;
import com.hb.cda.electricitybusiness.controller.dto.mapper.UserMapper;
import com.hb.cda.electricitybusiness.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountBusiness accountBusiness;
    private final UserMapper userMapper;

    public AccountController(AccountBusiness accountBusiness, UserMapper userMapper) {
        this.accountBusiness = accountBusiness;
        this.userMapper = userMapper;
    }


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody RegisterRequest dto) {
        User user = userMapper.convertToEntity(dto);
        User registeredUser = accountBusiness.registerUser(user);
        UserResponse userResponse = userMapper.userToUserResponse(registeredUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }


    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = accountBusiness.getAllUsers();
        List<UserResponse> userResponses = users.stream()
                .map(userMapper::userToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponses);
    }


    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = accountBusiness.getUserById(id);
        return ResponseEntity.ok(userMapper.userToUserResponse(user));
    }


    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getConnectedUser(Principal principal) {
        User user = accountBusiness.getAuthenticatedUserResponse(principal.getName());
        return ResponseEntity.ok(userMapper.userToUserResponse(user));
    }


    @PatchMapping("/{id}/email")
    public ResponseEntity<UserResponse> updateUserEmail(
            @PathVariable Long id,
            @Valid @RequestBody UserEmailUpdateDto dto
    ) {
        User updatedUser = accountBusiness.updateUserEmail(id, dto);
        return ResponseEntity.ok(userMapper.userToUserResponse(updatedUser));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        accountBusiness.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{userId}/password")
    public ResponseEntity<Map<String, String>> updatePassword(
            @PathVariable Long userId,
            @RequestBody PasswordUpdateRequestDTO request) {

        accountBusiness.updatePassword(userId, request.getOldPassword(), request.getNewPassword());

        return ResponseEntity.ok(Map.of("message", "Mot de passe mis à jour avec succès"));

    }


    @PostMapping("/{userId}/uploadProfilePicture")
    public ResponseEntity<PictureDetailsDTO> uploadProfilePicture(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "alt", required = false) String altText,
            @RequestParam(value = "isMain", defaultValue = "true") boolean isMain
    ) {
        PictureDetailsDTO dto = accountBusiness.uploadProfilePicture(userId, file, altText, isMain);
        return ResponseEntity.ok(dto);
    }
}
