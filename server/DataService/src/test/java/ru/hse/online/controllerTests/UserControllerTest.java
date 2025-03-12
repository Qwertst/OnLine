package ru.hse.online.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hse.online.controller.UserController;
import ru.hse.online.model.User;
import ru.hse.online.service.UserService;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("GET /api/v1/get/user/ - By Username")
    void getUserByUsernameSuccess() throws Exception {
        User user = User.builder().userId(UUID.randomUUID()).username("andrey").build();
        when(userService.getUserByName("alice")).thenReturn(user);

        mockMvc.perform(get("/api/v1/get/user/")
                        .param("username", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("andrey"));
    }

    @Test
    @DisplayName("POST /api/v1/create/user - Success")
    void createUserDuplicateUsername() throws Exception {
        User user = User.builder().userId(UUID.randomUUID()).username("anton").build();

        mockMvc.perform(post("/api/v1/create/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
        ).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/get/user/ - Invalid UUID Format")
    void getUserByIdInvalidUUID() throws Exception {

        mockMvc.perform(get("/api/v1/get/user/")
                        .param("userId", "invalid-uuid"))
                .andExpect(status().isBadRequest());
    }
}