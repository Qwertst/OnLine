package ru.hse.online.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hse.online.model.Path;
import ru.hse.online.service.PathService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class PathController {

    private final PathService pathService;

    @Operation(summary = "Get user's paths")
    @GetMapping("get/path/{userId}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paths retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Path.class))))
    })
    public ResponseEntity<List<Path>> getPaths(
            @Parameter(description = "User ID", required = true)
            @PathVariable UUID userId) {
        return ResponseEntity.ok(pathService.getPathsList(userId));
    }

    @Operation(summary = "Add new path")
    @PostMapping("create/path")
    @ApiResponses({
            @ApiResponse(responseCode = "204"),
    })
    public ResponseEntity<Void> addPath(
            @RequestBody
            @Schema(example = """
                    {
                        "key": {"userId": "770e8400-e29b-41d4-a716-446655440000"},
                        "polyline": "line"
                    }""")
            Path path) {
        try {
            pathService.addPath(path);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/remove/path/")
    public ResponseEntity<Void> removePath(
            @RequestParam(name = "userId") UUID userId,
            @RequestParam(name = "pathId") UUID pathId
    ) {
        pathService.removePath(userId, pathId);
        return ResponseEntity.noContent().build();
    }
}