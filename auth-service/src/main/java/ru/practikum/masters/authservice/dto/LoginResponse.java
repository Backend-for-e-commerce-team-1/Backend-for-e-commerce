package ru.practikum.masters.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ на успешную аутентификацию пользователя")
public class LoginResponse {


    @Schema(
            description = "JWT токен",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6W3sicm9sZUlkIjoiODE4Yjk4NjMtY2ExYS00ZmFiLThhYWMtNzQxZGYwYzc5ZmM3Iiwicm9sZU5hbWUiOiJST0xFX1VTRVIifV0sInVzZXJJZCI6Ijk3NWU5N2FlLWM0NGEtNDUwNy05ODE0LTA4NTM0ZDY5ODAzMiIsImVtYWlsIjoiam9obkBleGFtcGxlLmNvbSIsInVzZXJuYW1lIjoiam9obl9kb2UiLCJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNzY2NDc3OTczLCJleHAiOjE3NjY0ODE1NzN9.gxIJj1fUbKphoxRGxU2-rGoDk2tzmH84-IObCu75RwY",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String token;

    @Schema(
            description = "Время жизни токена в секундах",
            example = "3600",
            minimum = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long expiresIn;
}
