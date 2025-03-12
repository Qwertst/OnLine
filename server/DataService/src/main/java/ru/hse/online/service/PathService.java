package ru.hse.online.service;

import ru.hse.online.model.Path;

import java.util.List;
import java.util.UUID;

public interface PathService {
    List<Path> getPathsList(UUID userId);

    void addPath(Path path);

    void removePath(UUID userId, UUID pathId);
}