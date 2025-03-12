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
import ru.hse.online.model.User;
import ru.hse.online.service.FriendshipService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    @Operation(summary = "Get user's friends list")
    @GetMapping("get/friend/{userId}")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class))))
    })
    public ResponseEntity<List<User>> getFriends(
            @Parameter(description = "User ID", required = true)
            @PathVariable UUID userId) {
        return ResponseEntity.ok(friendshipService.getFriendsList(userId));
    }

    @Operation(summary = "Add friend relationship")
    @PostMapping("create/friend/")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
    })
    public ResponseEntity<Void> addFriend(
            @Parameter(description = "User ID", required = true)
            @RequestParam(name = "userId") UUID userId,
            @Parameter(description = "Friend ID", required = true)
            @RequestParam(name = "friendId") UUID friendId) {
        friendshipService.addFriend(userId, friendId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("remove/friend/")
    public ResponseEntity<Void> removeFriend(
            @RequestParam(name = "userId") UUID userId,
            @RequestParam(name = "friendId") UUID friendId
    ) {
        friendshipService.removeFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }
}