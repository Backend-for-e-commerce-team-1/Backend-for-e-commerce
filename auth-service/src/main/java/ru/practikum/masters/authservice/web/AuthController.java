package ru.practikum.masters.authservice.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practikum.masters.authservice.dto.*;
import ru.practikum.masters.authservice.exception.ParameterNotValidException;
import ru.practikum.masters.authservice.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AuthController {

    private static final int BEARER_PREFIX_LENGTH = "Bearer ".length();
    private final UserService userService;


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse addUser(@RequestBody @Valid RegisterRequest newUser) {
        log.info("Поступил запрос POST: /users/register на добавление пользователя: {}.", newUser);
        RegisterResponse registerResponse = userService.registerUser(newUser);
        log.info("Пользователь {} успешно зарегистрирован в системе.", registerResponse);
        return registerResponse;
    }

    @PostMapping("/login")
    public LoginResponse authUser(@RequestBody @Valid LoginRequest authUser) {
        log.info("Поступил запрос POST: /users/login на аутентификацию пользователя: {}.", authUser.getEmail());
        LoginResponse responseDto = userService.authenticate(authUser);
        log.info("Пользователь {} успешно прошел аутентификацию в системе.", authUser.getEmail());
        return responseDto;
    }

    @GetMapping("/profile")
    public RegisterResponse getUser(@RequestHeader(name = "Authorization", required = false) String authToken) {
        log.info("Поступил запрос GET: /users/profile");
        String token = extractTokenFromHeader(authToken);
        RegisterResponse registerResponse = userService.getUser(token);
        log.info("Профиль пользователя {} найден в системе.", registerResponse.getEmail());
        return registerResponse;
    }

    @PutMapping("/profile")
    public UpdateUserResponseDto updateUser(@RequestHeader(name = "Authorization") String authToken,
                                            @RequestBody @Valid UpdateUserRequestDto updateUser) {
        log.info("Поступил запрос PUT: /users/profile на редактирование пользователя {}.", updateUser.getEmail());
        String token = extractTokenFromHeader(authToken);
        UpdateUserResponseDto userDto = userService.updateUser(updateUser, token);
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
