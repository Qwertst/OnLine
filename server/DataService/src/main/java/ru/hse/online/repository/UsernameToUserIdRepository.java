package ru.hse.online.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import ru.hse.online.model.UsernameToUserId;

@Repository
public interface UsernameToUserIdRepository extends CassandraRepository<UsernameToUserId, String> {
}
