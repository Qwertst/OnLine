package ru.hse.online.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hse.online.model.UserStatistics;
import ru.hse.online.service.StatisticsService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "Get statistics data")
    @GetMapping("get/statistics/")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statistics retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserStatistics.class))))
    })
    public ResponseEntity<List<UserStatistics>> getStatistics(
            @Parameter(description = "User ID")
            @RequestParam(name = "userId")
            UUID userId,
            @Parameter(description = "Metric name")
            @RequestParam(name = "name")
            String name,
            @Parameter(description = "Start date (YYYY-MM-DD)")
            @RequestParam(name = "start")
            String startStr,
            @Parameter(description = "End date (YYYY-MM-DD)")
            @RequestParam(name = "end")
            String endStr) {
        LocalDate start = LocalDate.parse(startStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate end = LocalDate.parse(endStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return ResponseEntity.ok(
                statisticsService.getStatisticsForPeriod(userId, name, start, end)
        );
    }

    @PostMapping("create/statistics/")
    public ResponseEntity<Void> addStatistics(
            @Valid @RequestBody List<UserStatistics> stats
    ) {
        statisticsService.saveStatistics(stats);
        return ResponseEntity.noContent().build();
    }
}