package ru.hse.online.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@Table("user_statistics")
public class UserStatistics {
    @NotNull
    @PrimaryKey
    private final UserStatisticsKey key;

    @Column("value")
    @Schema(example = "100")
    private final Double value;


    @Data
    @Builder
    @PrimaryKeyClass
    public static class UserStatisticsKey implements Serializable {
        @PrimaryKeyColumn(name = "user_id", type = PrimaryKeyType.PARTITIONED)
        private final UUID userId;

        @PrimaryKeyColumn(name = "name", type = PrimaryKeyType.CLUSTERED)
        private final String name;

        @PrimaryKeyColumn(
                name = "timestamp",
                type = PrimaryKeyType.CLUSTERED,
                ordering = Ordering.DESCENDING
        )
        private final LocalDate timestamp;
    }
}
