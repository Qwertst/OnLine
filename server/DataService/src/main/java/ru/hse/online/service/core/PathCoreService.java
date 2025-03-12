package ru.hse.online.service.core;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hse.online.model.Path;
import ru.hse.online.repository.PathRepository;
import ru.hse.online.service.PathService;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PathCoreService implements PathService {
    private final PathRepository pathRepository;

    public List<Path> getPathsList(UUID userId) {
        return pathRepository.findByUserId(userId);
    }

    @Transactional
    public void addPath(Path path) {
        if (path.getKey() == null || path.getKey().getUserId() == null) {
            throw new IllegalArgumentException("Path must have a non-null user ID");
        }
        if (path.getKey().getPathId() == null) {
            path = Path.builder()
                    .key(Path.PathKey.builder()
                            .userId(path.getKey().getUserId())
                            .pathId(UUID.randomUUID())
                            .build())
                    .polyline(path.getPolyline())
                    .build();
        }
        pathRepository.save(path);
    }

    @Transactional
    public void removePath(UUID userId, UUID pathId) {
        pathRepository.deleteByUserIdAndPathId(userId, pathId);
    }
}
