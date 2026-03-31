package com.nbro.service;

import com.nbro.Exceptions.ErrorMessages;
import com.nbro.Exceptions.ResourceNotFoundException;
import com.nbro.domain.common.AppEnums;
import com.nbro.domain.dto.CreateUserRequestDTO;
import com.nbro.domain.dto.UpdateUserDTO;
import com.nbro.domain.dto.UserResponseDTO;
import com.nbro.domain.entity.User;
import com.nbro.helpers.MappingHelpers;
import com.nbro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

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

    @Transactional
    public void deactivateUser(UUID userID) {
        User rawUser = userRepository.findById(userID)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.RECORD_NOT_FOUND, "User with ID: " + userID)));
        rawUser.setActive(false);
        userRepository.save(rawUser);
        logger.info("User deactivated: {}", userID);
    }

    @Transactional
    public UserResponseDTO updateUserRole(UUID userID, AppEnums.Role newRole) {
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.RECORD_NOT_FOUND, "User with ID: " + userID)));
        user.setRole(newRole);
        User saved = userRepository.save(user);
        logger.info("User role updated: {} → {}", userID, newRole);
        return mappingHelpers.mapToDTO(saved);
    }

    @Transactional
    public UserResponseDTO updateUser(UUID userID, UpdateUserDTO request) {
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.RECORD_NOT_FOUND, "User with ID: " + userID)));
        user.setFullName(request.getFullName());
        user.setTeam(request.getTeam());
        User saved = userRepository.save(user);
        logger.info("User details updated: {}", userID);
        return mappingHelpers.mapToDTO(saved);
    }

    @Transactional
    public UserResponseDTO createUser(CreateUserRequestDTO request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException(ErrorMessages.USER_ALREADY_EXIST);
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
        logger.info("New user created: {}", request.getEmail());
        return mappingHelpers.mapToDTO(saved);
    }


}
