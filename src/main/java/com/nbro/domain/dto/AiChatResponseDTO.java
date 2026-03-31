package com.nbro.domain.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Response DTO for the AI chat assistant endpoint.
 */
@Getter
@Builder
public class AiChatResponseDTO {

    private String question;
    private String answer;
}
