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
        description = "Запрос на обновление профиля пользователя",
        requiredProperties = {}
)
public class UpdateProfileRequest {

    @Schema(
            description = "Новое имя пользователя (логин). Если не указано, остается прежним",
            example = "new_username",
            minLength = 2,
            maxLength = 50,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @Length(min = 2, max = 50)
    @NotBlank
    @NotNull
    private String username;

    @Schema(
            description = "Новый email пользователя. Если не указано, остается прежним",
            example = "new.email@example.com",
            minLength = 6,
            maxLength = 255,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            format = "email"
    )
    @Length(min = 6, max = 255)
    @NotBlank
    @Email
    private String email;

    public boolean hasUsername() {
        return !(username == null || username.isBlank());
    }

    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }

}
