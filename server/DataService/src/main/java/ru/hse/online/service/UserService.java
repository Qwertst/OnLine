package ru.hse.online.service;

import ru.hse.online.model.User;

import java.util.UUID;

public interface UserService {
    User getUserByName(String name);

    User getUserById(UUID id);

    void saveUser(User user);
}
