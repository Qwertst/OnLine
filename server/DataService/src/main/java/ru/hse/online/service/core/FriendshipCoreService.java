package ru.hse.online.service.core;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hse.online.model.User;
import ru.hse.online.repository.UserRepository;
import ru.hse.online.service.FriendshipService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FriendshipCoreService implements FriendshipService {

    private final UserRepository userRepository;
    @Autowired
    private CassandraTemplate cassandraTemplate;

    public List<User> getFriendsList(UUID userId) {
        return userRepository.findById(userId)
                .map(User::getFriends)
                .orElse(Collections.emptyList())
                .stream()
                .map(userRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Transactional
    public void addFriend(UUID userId, UUID friendId) {
        String query = String.format("UPDATE users SET friends = friends + [%s] WHERE user_id = %s", friendId, userId);
        cassandraTemplate.getCqlOperations().execute(query);
    }

    @Transactional
    public void removeFriend(UUID userId, UUID friendId) {
        String query = String.format("UPDATE users SET friends = friends - [%s] WHERE user_id = %s", friendId, userId);
        cassandraTemplate.getCqlOperations().execute(query);
    }
}

