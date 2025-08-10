package com.hb.cda.electricitybusiness.controller;

import com.hb.cda.electricitybusiness.business.AccountBusiness;
import com.hb.cda.electricitybusiness.controller.dto.UserEmailUpdateDto;
import com.hb.cda.electricitybusiness.controller.dto.UserResponse;
import com.hb.cda.electricitybusiness.controller.dto.mapper.UserMapper;
import com.hb.cda.electricitybusiness.controller.dto.RegisterRequest;
import com.hb.cda.electricitybusiness.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private AccountBusiness accountBusiness;
    private UserMapper userMapper;

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
        return new ResponseEntity<>(userResponses, HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = accountBusiness.getUserById(id);
        UserResponse userResponse = userMapper.userToUserResponse(user);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getConnectedUser(Principal connectedUserPrincipal) {
        User user = accountBusiness.getAuthenticatedUserResponse(connectedUserPrincipal.getName());
        UserResponse userResponse = userMapper.userToUserResponse(user);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PatchMapping("/{id}/email")
    public ResponseEntity<UserResponse> updateUserEmail(@PathVariable Long id, @Valid @RequestBody UserEmailUpdateDto emailUpdateDto) {
        User updatedUser = accountBusiness.updateUserEmail(id, emailUpdateDto);

        UserResponse updatedUserResponse = userMapper.userToUserResponse(updatedUser);

        return new ResponseEntity<>(updatedUserResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        accountBusiness.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/password/{email}")
    public String resetPassword(@PathVariable String email) {
        accountBusiness.resetPassword(email);
        return "Verifier votre mail pour réinitialiser votre mot de passe";
    }

    @PostMapping("/{userId}/uploadProfilePicture")
    public ResponseEntity<String> uploadProfilePicture(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "alt", required = false) String altText,
            @RequestParam(value = "isMain", defaultValue = "true") boolean isMain
    ) {

        String fileDownloadUri = accountBusiness.uploadProfilePicture(userId, file, altText, isMain);

        return new ResponseEntity<>(fileDownloadUri, HttpStatus.OK);
    }


}
