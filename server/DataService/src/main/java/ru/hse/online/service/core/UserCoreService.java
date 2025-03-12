package ru.hse.online.service.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hse.online.exceptions.EntityNotFoundException;
import ru.hse.online.model.User;
import ru.hse.online.model.UsernameToUserId;
import ru.hse.online.repository.UserRepository;
import ru.hse.online.repository.UsernameToUserIdRepository;
import ru.hse.online.service.UserService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCoreService implements UserService {
    private final UserRepository userRepository;
    private final UsernameToUserIdRepository usernameToUserIdRepository;

    public User getUserByName(String name) {
        return usernameToUserIdRepository.findById(name)
                .map(UsernameToUserId::getUserId)
                .flatMap(userRepository::findById)
                .orElseThrow(() -> new EntityNotFoundException("User with name \"" + name + "\" - not found"));
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
        usernameToUserIdRepository.save(
                UsernameToUserId.builder()
                        .username(user.getUsername())
                        .userId(user.getUserId())
                        .build()
        );
    }
}
