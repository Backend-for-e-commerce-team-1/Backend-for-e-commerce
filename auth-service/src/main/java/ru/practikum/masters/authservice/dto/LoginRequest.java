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
@Schema(description = "Запрос на аутентификацию пользователя")
public class LoginRequest {

    @Schema(
            description = "Email пользователя",
            example = "user@example.com",
            minLength = 6,
            maxLength = 255
    )
    @Length(min = 6, max = 255)
    @NotBlank
    @Email
    private String email;

    @Schema(
            description = "Пароль пользователя",
            example = "mySecurePassword123",
            minLength = 8,
            maxLength = 255
    )
    @Length(min = 8, max = 255)
    @NotBlank
    @NotNull
    private String password;

}
