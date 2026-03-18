package com.nbro.domain.dto;

import com.nbro.domain.common.AppEnums;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponseDTO {

    private UUID id;
    private String fullName;
    private String email;
    private AppEnums.Role role;
    private String team;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
}
