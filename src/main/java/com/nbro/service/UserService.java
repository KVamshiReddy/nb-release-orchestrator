package com.nbro.service;

import com.nbro.Exceptions.ResourceNotFoundException;
import com.nbro.domain.common.AppEnums;
import com.nbro.domain.dto.CreateUserRequestDTO;
import com.nbro.domain.dto.UpdateUserDTO;
import com.nbro.domain.dto.UserResponseDTO;
import com.nbro.domain.entity.User;
import com.nbro.helpers.MappingHelpers;
import com.nbro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MappingHelpers mappingHelpers;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(mappingHelpers::mapToDTO)
                .toList();
    }

    public UserResponseDTO getUserByID(UUID userID) {
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found With ID: " + userID));
        return mappingHelpers.mapToDTO(user);
    }

    public void deactivateUser(UUID userID) {
        User rawUser = userRepository.findById(userID).orElseThrow(() -> new ResourceNotFoundException("User Not Found With ID: " + userID));
        rawUser.setActive(false);
        User saved = userRepository.save(rawUser);
        mappingHelpers.mapToDTO(saved);
    }

    public UserResponseDTO updateUserRole(UUID userID, AppEnums.Role newRole) {
        User user = userRepository.findById(userID).orElseThrow(() -> new ResourceNotFoundException("User Not Found With ID: " + userID));
        user.setRole(newRole);
        User saved = userRepository.save(user);
        return mappingHelpers.mapToDTO(saved);
    }

    public UserResponseDTO updateUser(UUID userID, UpdateUserDTO request) {
        User user = userRepository.findById(userID).orElseThrow(() -> new ResourceNotFoundException("User Not Found With ID: " + userID));
        user.setFullName(request.getFullName());
        user.setTeam(request.getTeam());
        User saved = userRepository.save(user);
        return mappingHelpers.mapToDTO(saved);
    }

    public UserResponseDTO createUser(CreateUserRequestDTO request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("User with email already exists: " + request.getEmail());
        }
        User cleanUser = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role(request.getRole())
                .team(request.getTeam())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .build();
        User saved = userRepository.save(cleanUser);
        return mappingHelpers.mapToDTO(saved);

    }


}
