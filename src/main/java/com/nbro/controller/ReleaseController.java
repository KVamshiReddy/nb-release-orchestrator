package com.nbro.controller;

import com.nbro.domain.dto.ReleaseRequestDTO;
import com.nbro.domain.entity.Release;
import com.nbro.service.ReleaseService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/releases")
public class ReleaseController {

    private final ReleaseService releaseService;

    @Operation(summary = "Create a New Release Request")
    @PostMapping("/")
    public Release createRelease(@RequestBody ReleaseRequestDTO request) {
        return releaseService.createRelease(request);
    }

    @Operation(summary = "Get a Release By it's Unique UUID")
    @GetMapping("/{id}")
    public Release getReleaseById(@PathVariable UUID id) {
        return releaseService.getReleaseById(id);
    }

    @Operation(summary = "Get All Releases")
    @GetMapping("/")
    public List<Release> getAllReleases() {
        return releaseService.getAllReleases();
    }


}
