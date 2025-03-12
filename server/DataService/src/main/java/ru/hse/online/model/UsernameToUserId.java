package ru.hse.online.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Data
@Builder
@Table("username_to_user_id")
public class UsernameToUserId {
    @NotNull
    @PrimaryKey("username")
    private final String username;

    @NotNull
    @Column("user_id")
    private final UUID userId;
}
