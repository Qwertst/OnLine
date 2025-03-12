package ru.hse.online.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@Table("paths")
@Schema(description = "Path entity")
public class Path {
    @NotNull
    @PrimaryKey
    @Schema(description = "Path composite key")
    private final PathKey key;

    @NotNull
    @Column("polyline")
    @Schema(description = "Polyline coordinates")
    private final String polyline;

    @Data
    @Builder
    @PrimaryKeyClass
    public static class PathKey implements Serializable {
        @PrimaryKeyColumn(name = "user_id", type = PrimaryKeyType.PARTITIONED)
        private final UUID userId;

        @PrimaryKeyColumn(name = "path_id", type = PrimaryKeyType.CLUSTERED)
        private final UUID pathId;
    }
}
