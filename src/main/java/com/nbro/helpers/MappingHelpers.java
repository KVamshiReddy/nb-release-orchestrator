package com.nbro.helpers;

import com.nbro.domain.dto.UserResponseDTO;
import com.nbro.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class MappingHelpers {

    public UserResponseDTO mapToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole());
        dto.setTeam(user.getTeam());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

}
