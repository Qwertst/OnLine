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
import ru.hse.online.controller.PathController;
import ru.hse.online.exceptions.EntityNotFoundException;
import ru.hse.online.model.Path;
import ru.hse.online.service.PathService;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PathController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class PathControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PathService pathService;

    @Test
    @DisplayName("GET /api/v1/get/path/{userId} - Empty Path List")
    void getPathsEmptyList() throws Exception {
        UUID userId = UUID.randomUUID();
        when(pathService.getPathsList(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/get/path/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("DELETE /api/v1/remove/path/ - Path Not Found")
    void removePathNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID pathId = UUID.randomUUID();

        doThrow(new EntityNotFoundException("Path not found"))
                .when(pathService).removePath(userId, pathId);

        mockMvc.perform(delete("/api/v1/remove/path/")
                        .param("userId", userId.toString())
                        .param("pathId", pathId.toString()))
                .andExpect(status().isNotFound());
    }
}
