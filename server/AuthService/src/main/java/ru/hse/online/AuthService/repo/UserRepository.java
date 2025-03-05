package ru.hse.online.AuthService.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hse.online.AuthService.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
