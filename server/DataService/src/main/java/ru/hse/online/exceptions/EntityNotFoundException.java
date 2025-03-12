package ru.hse.online.exceptions;

import java.util.UUID;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entity, UUID id) {
        super(entity + " with ID: " + id + " not found");
    }
}
