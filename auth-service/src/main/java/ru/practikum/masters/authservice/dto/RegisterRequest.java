package ru.practikum.masters.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        description = "Запрос на регистрацию нового пользователя в системе",
        requiredProperties = {"username", "email", "password"}
)
public class RegisterRequest {

    @Schema(
            description = "Уникальное имя пользователя для входа в систему",
            example = "john_doe",
            minLength = 2,
            maxLength = 50,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Length(min = 2, max = 50)
    @NotBlank
    @NotNull
    private String username;

    @Schema(
            description = "Электронная почта пользователя",
            example = "user@example.com",
            minLength = 6,
            maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED,
            format = "email"
    )
    @Length(min = 6, max = 255)
    @NotBlank
    @Email
    private String email;

    @Schema(
            description = "Пароль для доступа к аккаунту",
            example = "SecurePassword123!",
            minLength = 8,
            maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Length(min = 8, max = 255)
    @NotBlank
    @NotNull
    private String password;
}
