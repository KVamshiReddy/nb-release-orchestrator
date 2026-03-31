package com.nbro.controller;

import com.nbro.domain.dto.CalendarReleaseDTO;
import com.nbro.domain.dto.DashboardStatsDTO;
import com.nbro.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "KPI stats and calendar for the dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "Get release counts by status for KPI cards")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }

    @GetMapping("/calendar")
    @Operation(summary = "Get scheduled releases ordered by date for calendar view")
    public ResponseEntity<List<CalendarReleaseDTO>> getCalendar() {
        return ResponseEntity.ok(dashboardService.getCalendar());
    }
}
