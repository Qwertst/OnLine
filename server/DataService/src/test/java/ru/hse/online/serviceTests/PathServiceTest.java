package ru.hse.online.serviceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hse.online.model.Path;
import ru.hse.online.repository.PathRepository;
import ru.hse.online.service.core.PathCoreService;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    @Mock
    private PathRepository pathRepository;

    @InjectMocks
    private PathCoreService pathCoreService;

    private UUID userId;
    private UUID pathId1;
    private Path path1;
    private Path path2;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        pathId1 = UUID.randomUUID();
        UUID pathId2 = UUID.randomUUID();

        Path.PathKey pathKey1 = Path.PathKey.builder()
                .userId(userId)
                .pathId(pathId1)
                .build();

        Path.PathKey pathKey2 = Path.PathKey.builder()
                .userId(userId)
                .pathId(pathId2)
                .build();

        path1 = Path.builder()
                .key(pathKey1)
                .polyline("polyline1")
                .build();

        path2 = Path.builder()
                .key(pathKey2)
                .polyline("polyline2")
                .build();
    }

    @Test
    void getPathsListReturnsListOfPaths() {
        List<Path> expectedPaths = Arrays.asList(path1, path2);
        when(pathRepository.findByUserId(userId)).thenReturn(expectedPaths);

        List<Path> actualPaths = pathCoreService.getPathsList(userId);

        assertEquals(expectedPaths.size(), actualPaths.size());
        assertEquals(expectedPaths.get(0), actualPaths.get(0));
        assertEquals(expectedPaths.get(1), actualPaths.get(1));

        verify(pathRepository).findByUserId(userId);
    }

    @Test
    void addPathCallsSaveMethod() {
        pathCoreService.addPath(path1);
        verify(pathRepository).save(path1);
    }

    @Test
    void removePathCallsDeleteMethod() {
        pathCoreService.removePath(userId, pathId1);
        verify(pathRepository).deleteByUserIdAndPathId(userId, pathId1);
    }
}