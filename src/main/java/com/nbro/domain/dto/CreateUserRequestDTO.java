package com.nbro.domain.dto;

import com.nbro.domain.common.AppEnums;
import lombok.Data;

@Data
public class CreateUserRequestDTO {

    private String fullName;
    private String email;
    private String password;
    private String team;
    private AppEnums.Role role;

}
