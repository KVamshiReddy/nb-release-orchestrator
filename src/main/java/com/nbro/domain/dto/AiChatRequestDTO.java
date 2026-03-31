package com.nbro.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for the AI chat assistant endpoint.
 */
@Getter
@Setter
@NoArgsConstructor
public class AiChatRequestDTO {

    /** The user's question about releases or the system */
    private String message;
}
