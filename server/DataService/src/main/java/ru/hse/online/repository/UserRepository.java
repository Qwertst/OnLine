package ru.hse.online.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import ru.hse.online.model.User;

import java.util.UUID;

@Repository
public interface UserRepository extends CassandraRepository<User, UUID> {
}

