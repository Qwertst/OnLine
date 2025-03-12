package ru.hse.online.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Schema(description = "User entity")
@Table("users")
public class User {
    @NotNull
    @PrimaryKey("user_id")
    @Schema(description = "Unique user ID", example = "770e8400-e29b-41d4-a716-446655440000")
    private final UUID userId;

    @Column("username")
    @Schema(description = "Username", example = "anton")
    private final String username;

    @Column("friends")
    @Schema(description = "List of friend IDs")
    private final List<UUID> friends;
}