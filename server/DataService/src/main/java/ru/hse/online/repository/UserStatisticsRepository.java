package ru.hse.online.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hse.online.model.UserStatistics;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserStatisticsRepository extends CassandraRepository<UserStatistics, UUID> {
    @Query("SELECT * FROM user_statistics WHERE user_id = ?0 AND name = ?1 AND timestamp >= ?2 AND timestamp <= ?3")
    List<UserStatistics> findByUserIdAndNameAndTimestampBetween(
            UUID userId,
            String name,
            LocalDate start,
            LocalDate end
    );
}

