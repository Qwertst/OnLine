package ru.hse.online.service;

import ru.hse.online.model.User;

import java.util.List;
import java.util.UUID;

public interface FriendshipService {
    List<User> getFriendsList(UUID userId);

    void addFriend(UUID userId, UUID friendId);

    void removeFriend(UUID userId, UUID friendId);
}
