package com.nbro.controller;

import com.nbro.domain.entity.Issue;
import com.nbro.service.AIService;
import com.nbro.service.JiraService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class JiraController {

    private final JiraService jiraService;
    private final AIService aiService;

    @Operation(summary = "Get an Issue based on it's unique key")
    @GetMapping("/api/issues/{key}")
    public Issue getRawIssue(@PathVariable String key) {
        return jiraService.getRawIssue(key);
    }

    @Operation(summary = "Get response from Groq API")
    @PostMapping("/api/ai/test")
    public String getAiResponse(@RequestParam String prompt) {
        return aiService.generateResponse(prompt);
    }


}
