package ru.practikum.masters.authservice.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.masters.exceptions.ErrorResponse;
import ru.practikum.masters.authservice.dto.*;
import ru.practikum.masters.authservice.exception.ParameterNotValidException;
import ru.practikum.masters.authservice.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Authentication Controller", description = "API для регистрации, аутентификации и управления профилями пользователей")
public class AuthController {

    private static final int BEARER_PREFIX_LENGTH = "Bearer ".length();
    private final UserService userService;

    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создает нового пользователя в системе. Возвращает данные зарегистрированного пользователя."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Пользователь успешно создан",
                    content = @Content(schema = @Schema(implementation = RegisterResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидные данные пользователя",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Пользователь с таким email уже существует"
            )
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse addUser(@RequestBody @Valid RegisterRequest newUser) {
        log.info("Поступил запрос POST: /users/register на добавление пользователя: {}.", newUser);
        RegisterResponse registerResponse = userService.registerUser(newUser);
        log.info("Пользователь {} успешно зарегистрирован в системе.", registerResponse);
        return registerResponse;
    }


    @Operation(
            summary = "Аутентификация пользователя",
            description = "Авторизует пользователя по email и паролю. Возвращает JWT токен."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная аутентификация",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Неверные учетные данные"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидные данные запроса"
            )
    })
    @PostMapping("/login")
    public LoginResponse authUser(@RequestBody @Valid LoginRequest authUser) {
        log.info("Поступил запрос POST: /users/login на аутентификацию пользователя: {}.", authUser.getEmail());
        LoginResponse responseDto = userService.authenticate(authUser);
        log.info("Пользователь {} успешно прошел аутентификацию в системе.", authUser.getEmail());
        return responseDto;
    }


    @Operation(
            summary = "Получение профиля пользователя",
            description = "Возвращает профиль текущего аутентифицированного пользователя по JWT токену.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Профиль пользователя успешно получен",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Требуется авторизация"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден"
            )
    })
    @GetMapping("/profile")
    public UserProfileResponse getUser(@RequestHeader(name = "Authorization", required = false) String authToken) {
        log.info("Поступил запрос GET: /users/profile");
        String token = extractTokenFromHeader(authToken);
        UserProfileResponse userProfile = userService.getUserProfile(token);
        log.info("Профиль пользователя {} найден в системе.", userProfile.getEmail());
        return userProfile;
    }


    @Operation(
            summary = "Обновление профиля пользователя",
            description = "Обновляет данные профиля текущего аутентифицированного пользователя.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Профиль успешно обновлен",
                    content = @Content(schema = @Schema(implementation = UpdateUserResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидные данные"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Требуется авторизация"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден"
            )
    })
    @PutMapping("/profile")
    public UpdateUserResponseDto updateUser(@RequestHeader(name = "Authorization") String authToken,
                                            @RequestBody @Valid UpdateProfileRequest updateUser) {
        log.info("Поступил запрос PUT: /users/profile на редактирование пользователя {}.", updateUser.getEmail());
        String token = extractTokenFromHeader(authToken);
        UpdateUserResponseDto userDto = userService.updateUserProfile(updateUser, token);
        log.info("Профиль пользователя {} успешно обновлен в системе.", updateUser.getEmail());
        return userDto;
    }

    /**
     * Извлекаем JWT токен из заголовка Authorization
     *
     * @param authorizationHeader - заголовок в формате "Bearer {token}"
     * @return JWT токен без префикса "Bearer "
     * @throws IllegalArgumentException если заголовок невалидный
     */
    private String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            log.error("Отсутствует заголовок авторизации Authorization: Bearer {token}");
            throw new ParameterNotValidException("Authorization",
                    "Отсутствует заголовок авторизации Authorization: Bearer {token}");
        }

        if (!authorizationHeader.startsWith("Bearer ")) {
            log.error("Заголовок авторизации Authorization: Bearer {token} должен начинаться с 'Bearer '");
            throw new ParameterNotValidException("Authorization",
                    "ЗЗаголовок авторизации Authorization: Bearer {token} должен начинаться с 'Bearer '");
        }

        // Убираем "Bearer" и возвращаем только токен
        return authorizationHeader.substring(BEARER_PREFIX_LENGTH);
    }

}
