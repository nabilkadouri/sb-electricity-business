package com.hb.cda.electricitybusiness.service;

import com.hb.cda.electricitybusiness.dto.UserEmailUpdateDto;
import com.hb.cda.electricitybusiness.dto.UserResponse;
import com.hb.cda.electricitybusiness.dto.UserUpdateRequest;
import com.hb.cda.electricitybusiness.dto.mapper.UserMapper;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.repository.UserRepository;
import com.hb.cda.electricitybusiness.security.dto.auth.RegisterRequest;

import jakarta.transaction.Transactional;
import org.hibernate.sql.Update;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService  implements UserDetailsService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec email: " + email));
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
        user.setPicture("images/default_avatar.png");
        user.setCodeCheck(null);

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
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID: " + id);
        }
        userRepository.deleteById(id);
    }

}
