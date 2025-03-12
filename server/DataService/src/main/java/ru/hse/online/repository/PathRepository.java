package ru.hse.online.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hse.online.model.Path;

import java.util.List;
import java.util.UUID;

@Repository
public interface PathRepository extends CassandraRepository<Path, UUID> {
    @Query("SELECT * FROM paths WHERE user_id = ?0")
    List<Path> findByUserId(UUID userId);

    @Query("DELETE FROM paths WHERE user_id = ?0 AND path_id = ?1")
    void deleteByUserIdAndPathId(UUID userId, UUID pathId);
}
