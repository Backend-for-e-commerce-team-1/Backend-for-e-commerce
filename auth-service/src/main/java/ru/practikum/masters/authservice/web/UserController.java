package ru.practikum.masters.authservice.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practikum.masters.authservice.dto.*;
import ru.practikum.masters.authservice.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserService userService;


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody @Valid NewUserRequestDto newUser) {
        log.info("Поступил запрос POST: /users/register на добавление пользователя: {}.", newUser);
        UserDto userDto = userService.addUser(newUser);
        log.info("Пользователь {} успешно зарегистрирован в системе.", userDto);
        return userDto;
    }

    @PostMapping("/login")
    public AuthUserResponseDto authUser(@RequestBody @Valid AuthUserRequestDto authUser) {
        log.info("Поступил запрос POST: /users/login на аутентификацию пользователя: {}.", authUser.getEmail());
        AuthUserResponseDto responseDto = userService.authUser(authUser);
        log.info("Пользователь {} успешно прошел аутентификацию в системе.", authUser.getEmail());
        return responseDto;
    }

    @GetMapping("/profile")
    public UserDto getUser(@RequestHeader(name = "Authorization") String authToken) {
        log.info("Поступил запрос GET: /users/profile");
        String token = extractTokenFromHeader(authToken);
        UserDto userDto = userService.getUser(token);
        log.info("Профиль пользователя {} найден в системе.", userDto.getEmail());
        return userDto;
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
     * @param authorizationHeader - заголовок в формате "Bearer {token}"
     * @return JWT токен без префикса "Bearer "
     * @throws IllegalArgumentException если заголовок невалидный
     */
    private String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader == null) {
            log.error("Заголовок авторизации отсутствует");
            throw new IllegalArgumentException("Заголовок авторизации отсутствует");
        }

        if (!authorizationHeader.startsWith("Bearer ")) {
            log.error("Заголовок авторизации должен начинаться с 'Bearer '");
            throw new IllegalArgumentException("Заголовок авторизации должен начинаться с 'Bearer '");
        }

        // Убираем "Bearer" и возвращаем только токен
        return authorizationHeader.substring(7);
    }

}
