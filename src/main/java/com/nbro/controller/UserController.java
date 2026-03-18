package com.nbro.controller;

import com.nbro.domain.common.AppEnums;
import com.nbro.domain.dto.CreateUserRequestDTO;
import com.nbro.domain.dto.UpdateUserDTO;
import com.nbro.domain.dto.UserResponseDTO;
import com.nbro.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('RELEASE_MANAGER')")
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get a List of All Users")
    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Get a User By ID")
    @GetMapping("/{id}")
    public UserResponseDTO getUserByID(@PathVariable UUID id) {
        return userService.getUserByID(id);
    }

    @Operation(summary = "Create a new User")
    @PostMapping
    public UserResponseDTO createUser(@RequestBody CreateUserRequestDTO userDto) {
        return userService.createUser(userDto);
    }

    @Operation(summary = "Update a User Role")
    @PatchMapping("/{id}/role")
    public UserResponseDTO updateUserRole(@PathVariable UUID id, @RequestParam AppEnums.Role role) {
        return userService.updateUserRole(id, role);
    }

    @Operation(summary = "Update User Details")
    @PutMapping("/{id}")
    public UserResponseDTO updateUser(@PathVariable UUID id, @RequestBody UpdateUserDTO updateDto) {
        return userService.updateUser(id, updateDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deactivateUser(@PathVariable UUID id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok("User deactivated successfully");
    }

}
