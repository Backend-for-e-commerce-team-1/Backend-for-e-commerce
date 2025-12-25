package ru.practikum.masters.authservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        description = "Профиль пользователя",
        requiredProperties = {"userId", "username", "email", "createdAt"}
)
public class UserProfileResponse {

    @Schema(
            description = "Уникальный идентификатор пользователя в системе",
            example = "123e4567-e89b-12d3-a456-426614174000",
            requiredMode = Schema.RequiredMode.REQUIRED,
            format = "uuid"
    )
    private UUID userId;

    @Schema(
            description = "Имя пользователя (логин)",
            example = "john_doe",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;

    @Schema(
            description = "Электронная почта пользователя",
            example = "john.doe@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED,
            format = "email"
    )
    private String email;

    @Schema(
            description = "Дата и время создания учетной записи пользователя",
            example = "2025-12-23 14:30:00",
            requiredMode = Schema.RequiredMode.REQUIRED,
            pattern = "yyyy-MM-dd HH:mm:ss",
            type = "string"
    )
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
