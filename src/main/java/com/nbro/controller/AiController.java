package com.nbro.controller;

import com.nbro.domain.dto.AiChatRequestDTO;
import com.nbro.domain.dto.AiChatResponseDTO;
import com.nbro.domain.dto.AiRiskAssessmentDTO;
import com.nbro.domain.dto.AiSummaryDTO;
import com.nbro.service.ReleaseAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "AI", description = "AI-powered risk scoring, summary generation, and chat assistant")
public class AiController {

    private final ReleaseAiService releaseAiService;

    @PostMapping("/releases/{id}/risk")
    @Operation(summary = "Generate a new AI risk assessment for a release")
    public ResponseEntity<AiRiskAssessmentDTO> generateRisk(@PathVariable UUID id) {
        return ResponseEntity.ok(releaseAiService.generateRisk(id));
    }

    @GetMapping("/releases/{id}/risk")
    @Operation(summary = "Get the latest AI risk assessment for a release")
    public ResponseEntity<AiRiskAssessmentDTO> getLatestRisk(@PathVariable UUID id) {
        return ResponseEntity.ok(releaseAiService.getLatestRisk(id));
    }

    @PostMapping("/releases/{id}/summary")
    @Operation(summary = "Generate and save an AI summary for a release")
    public ResponseEntity<AiSummaryDTO> generateSummary(@PathVariable UUID id) {
        return ResponseEntity.ok(releaseAiService.generateSummary(id));
    }

    @GetMapping("/releases/{id}/summary")
    @Operation(summary = "Get the stored AI summary for a release")
    public ResponseEntity<AiSummaryDTO> getSummary(@PathVariable UUID id) {
        return ResponseEntity.ok(releaseAiService.getSummary(id));
    }

    @PostMapping("/assistant/chat")
    @Operation(summary = "Ask the AI assistant a question about releases")
    public ResponseEntity<AiChatResponseDTO> chat(@RequestBody AiChatRequestDTO request) {
        return ResponseEntity.ok(releaseAiService.chat(request.getMessage()));
    }
}
