package ru.hse.online.controllerTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hse.online.controller.FriendshipController;
import ru.hse.online.exceptions.EntityNotFoundException;
import ru.hse.online.model.User;
import ru.hse.online.service.FriendshipService;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FriendshipController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class FriendshipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FriendshipService friendshipService;

    @Test
    @DisplayName("GET /api/v1/get/friend/{userId} - Success")
    void getFriendsSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        User friend = User.builder().userId(UUID.randomUUID()).username("friend").build();

        when(friendshipService.getFriendsList(userId)).thenReturn(List.of(friend));

        mockMvc.perform(get("/api/v1/get/friend/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("friend"));
    }

    @Test
    @DisplayName("POST /api/v1/create/friend- Add Friend Success")
    void addFriendSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID friendId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/create/friend/")
                        .param("userId", userId.toString())
                        .param("friendId", friendId.toString()))
                .andExpect(status().isOk());

        verify(friendshipService).addFriend(userId, friendId);
    }

    @Test
    @DisplayName("DELETE /api/v1/remove/friend - Remove Friend Not Found")
    void removeFriendNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID friendId = UUID.randomUUID();

        doThrow(new EntityNotFoundException("Friend not found"))
                .when(friendshipService).removeFriend(userId, friendId);

        mockMvc.perform(delete("/api/v1/remove/friend/")
                        .param("userId", userId.toString())
                        .param("friendId", friendId.toString()))
                .andExpect(status().isNotFound());
    }
}
