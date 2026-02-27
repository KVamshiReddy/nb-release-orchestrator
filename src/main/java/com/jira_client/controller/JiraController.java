package com.jira_client.controller;

import com.jira_client.service.JiraService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JiraController {

    private final JiraService jiraService;

    @Operation(summary = "Get an Issue based on it's unique key")
    @GetMapping("/api/issues/{key}")
    public String getRawIssue(@PathVariable String key) {
        return jiraService.getRawIssue(key);
    }
}
