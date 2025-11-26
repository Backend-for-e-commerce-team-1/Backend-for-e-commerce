package ru.practikum.masters.authservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.practicum.masters.securitylib.service.JwtService;
import ru.practikum.masters.authservice.dto.*;
import ru.practikum.masters.authservice.exception.AuthenticationException;
import ru.practikum.masters.authservice.exception.DuplicateUserException;
import ru.practikum.masters.authservice.exception.NotFoundException;
import ru.practikum.masters.authservice.mapper.UserMapper;
import ru.practikum.masters.authservice.model.Role;
import ru.practikum.masters.authservice.model.RoleType;
import ru.practikum.masters.authservice.model.User;
import ru.practikum.masters.authservice.repository.RoleRepository;
import ru.practikum.masters.authservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private User user;
    private UserDetails userDetails;
    private RegisterRequest newUserRequestDto;

    private final UUID userId = UUID.randomUUID();
    private final UUID roleId = UUID.randomUUID();
    private final LocalDateTime createdAt = LocalDateTime.now();

    private final Role role = new Role(roleId, RoleType.ROLE_USER);

    @BeforeEach
    void setUp() {
        user = new User(userId, "john_doe", "john@example.com", "StrongPassword123", createdAt, createdAt.plusHours(1),
                List.of(role));
        newUserRequestDto = new RegisterRequest("john_doe", "john@example.com", "StrongPassword123");
        userDetails = new UserDetails(userId, "john_doe", "john@example.com", createdAt, null, List.of(role));
        userService = new UserServiceImpl(userRepository, roleRepository, jwtService, passwordEncoder, userMapper);
    }


    /* -------------------------------------------------------------------------
     * -------------------------------------------------------------------------
     *  userService.addUser
     * -------------------------------------------------------------------------
     * -------------------------------------------------------------------------
     */


    /**
     * Тест: Успешное добавление нового пользователя
     * Проверяет, что когда все данные пользователя уникальны и корректны,
     * система успешно сохраняет пользователя и возвращает его DTO представление
     */
    @Test
    void shouldAddNewUserSuccessfully() {
        // Настройка поведения мок-объектов:
        // - При вызове шифрования пароля возвращаем исходный пароль (для упрощения теста)
        // - При сохранении пользователя в репозитории возвращаем сохраненного пользователя
        when(passwordEncoder.encode(anyString())).thenReturn("StrongPassword123");
        when(roleRepository.findByRoleName(RoleType.ROLE_USER)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Вызов тестируемого метода - регистрация нового пользователя
        RegisterResponse result = userServiceImpl.registerUser(newUserRequestDto);

        // Проверки результатов:
        // - Убеждаемся, что результат не null
        // - Сравниваем все поля возвращенного DTO с ожидаемыми значениями
        assertNotNull(result);
        assertEquals(userDetails.getUserId(), result.getUserId());
        assertEquals(userDetails.getUsername(), result.getUsername());
        assertEquals(userDetails.getEmail(), result.getEmail());
        assertEquals(userDetails.getCreatedAt(), result.getCreatedAt());

        // Проверка вызовов зависимостей:
        // - Убеждаемся, что пароль был зашифрован ровно один раз
        // - Убеждаемся, что пользователь был сохранен в репозитории ровно один раз
        // - Убеждаемся, что проверка уникальности (findAll) была выполнена
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).findAll();
    }

    /**
     * Тест: Ошибка при добавлении пользователя с уже существующим email
     * Проверяет, что система выбрасывает исключение DataConflictException,
     * когда пытаются зарегистрировать пользователя с email, который уже используется
     */
    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Создаем существующего пользователя с таким же email
        User existingUser = new User(
                UUID.randomUUID(),
                "different_user",
                "john@example.com", // Тот же email, что и у нового пользователя
                "AnotherPassword123",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1).plusHours(1),
                List.of(role)
        );

        // Настройка поведения мок-объекта репозитория:
        // - При запросе всех пользователей возвращаем список с существующим пользователем
        when(userRepository.findAll()).thenReturn(List.of(existingUser));

        // Проверяем, что при попытке регистрации выбрасывается исключение
        DuplicateUserException exception = assertThrows(
                DuplicateUserException.class,
                () -> userServiceImpl.registerUser(newUserRequestDto)
        );

        // Проверяем сообщение об ошибке
        assertEquals("Email john@example.com уже используется в системе другим пользователем.",
                exception.getMessage());

        // Проверка, что операции не были выполнены:
        // - Сохранение пользователя не должно происходить при конфликте
        // - Шифрование пароля не должно происходить при конфликте
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    /**
     * Тест: Ошибка при добавлении пользователя с уже существующим username
     * Проверяет, что система выбрасывает исключение DataConflictException,
     * когда пытаются зарегистрировать пользователя с username, который уже используется
     */
    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        // Создаем существующего пользователя с таким же username
        User existingUser = new User(
                UUID.randomUUID(),
                "john_doe", // Тот же username, что и у нового пользователя
                "different@example.com",
                "AnotherPassword123",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1).plusHours(1),
                List.of(role)
        );

        // Настройка поведения мок-объекта репозитория:
        // - При запросе всех пользователей возвращаем список с существующим пользователем
        when(userRepository.findAll()).thenReturn(List.of(existingUser));

        // Проверяем, что при попытке регистрации выбрасывается исключение
        DuplicateUserException exception = assertThrows(
                DuplicateUserException.class,
                () -> userServiceImpl.registerUser(newUserRequestDto)
        );

        // Проверяем сообщение об ошибке
        assertEquals("Имя пользователя john_doe уже используется в системе другим пользователем.",
                exception.getMessage());

        // Проверка, что операции не были выполнены при конфликте:
        // - Пользователь не должен быть сохранен
        // - Пароль не должен быть зашифрован
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    /**
     * Тест: Ошибка при добавлении пользователя с уже существующими email и username
     * Проверяет, что система выбрасывает исключение при двойном конфликте,
     * и проверяет приоритет проверок (сначала email, затем username)
     */
    @Test
    void shouldThrowExceptionWhenBothEmailAndUsernameAlreadyExist() {
        // Создаем существующего пользователя с таким же email И username
        User existingUser = new User(
                UUID.randomUUID(),
                "john_doe", // Тот же username
                "john@example.com", // Тот же email
                "AnotherPassword123",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1).plusHours(1),
                List.of(role)
        );

        // Настройка поведения мок-объекта репозитория:
        // - При запросе всех пользователей возвращаем список с конфликтующим пользователем
        when(userRepository.findAll()).thenReturn(List.of(existingUser));

        // Проверяем, что при попытке регистрации выбрасывается исключение
        DuplicateUserException exception = assertThrows(
                DuplicateUserException.class,
                () -> userServiceImpl.registerUser(newUserRequestDto)
        );

        // Проверяем, что исключение связано с email
        assertEquals("Email john@example.com уже используется в системе другим пользователем.",
                exception.getMessage());

        // Проверка, что при конфликте не выполняются операции сохранения и шифрования:
        // - Пользователь не сохраняется в базу
        // - Пароль не шифруется
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    /* -------------------------------------------------------------------------
     * -------------------------------------------------------------------------
     *  userService.authUser
     * -------------------------------------------------------------------------
     * -------------------------------------------------------------------------
     */


    /**
     * Тест: Успешная аутентификация с правильными учетными данными
     * Проверяет, что когда пользователь вводит правильный email и пароль,
     * система возвращает JWT токен и время его действия
     */
    @Test
    void shouldReturnTokenWhenCredentialsAreCorrect() {
        // Подготовка тестовых данных
        LoginRequest authRequest = new LoginRequest("john@example.com", "StrongPassword123");

        // Настройка поведения мок-объектов:
        // - При поиске по email возвращаем существующего пользователя
        // - При проверке пароля возвращаем true
        // - При генерации токена возвращаем тестовый токен
        // - При запросе времени жизни токена возвращаем 3600 секунд
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("StrongPassword123", "StrongPassword123")).thenReturn(true);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId().toString());
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        when(jwtService.generateToken(claims, user.getEmail())).thenReturn("jwt-token");
        when(jwtService.getExpirationInSeconds()).thenReturn(3600L);

        // Выполнение тестируемого метода
        LoginResponse result = userServiceImpl.authenticate(authRequest);

        // Проверка результатов:
        // - Убеждаемся, что возвращен корректный JWT токен
        // - Убеждаемся, что время жизни токена установлено правильно
        assertEquals("jwt-token", result.getToken());
        assertEquals(3600L, result.getExpiresIn());
    }

    /**
     * Тест: Ошибка аутентификации при неверном email
     * Проверяет, что система выбрасывает исключение, когда пользователь с указанным email не найден
     */
    @Test
    void shouldThrowExceptionWhenEmailIsWrong() {
        // Подготовка тестовых данных с несуществующим email
        LoginRequest authRequest = new LoginRequest("wrong@example.com", "StrongPassword123");

        // Настройка поведения мок-объекта репозитория:
        // - При поиске по email возвращаем пустой Optional (пользователь не найден)
        when(userRepository.findByEmail("wrong@example.com")).thenReturn(Optional.empty());

        // Проверка, что при вызове метода аутентификации выбрасывается ожидаемое исключение
        assertThrows(AuthenticationException.class, () -> userServiceImpl.authenticate(authRequest));
    }

    /**
     * Тест: Ошибка аутентификации при неверном пароле
     * Проверяет, что система выбрасывает исключение, когда email правильный, но пароль неверный
     */
    @Test
    void shouldThrowExceptionWhenPasswordIsWrong() {
        // Подготовка тестовых данных с правильным email, но неверным паролем
        LoginRequest authRequest = new LoginRequest("john@example.com", "WrongPassword");

        // Настройка поведения мок-объектов:
        // - При поиске по email возвращаем существующего пользователя
        // - При проверке пароля возвращаем false (пароль не совпадает)
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword", "StrongPassword123")).thenReturn(false);

        // Проверка, что при неверном пароле выбрасывается исключение аутентификации
        assertThrows(AuthenticationException.class, () -> userServiceImpl.authenticate(authRequest));
    }


    /* -------------------------------------------------------------------------
     * -------------------------------------------------------------------------
     *  userService.getUser
     * -------------------------------------------------------------------------
     * -------------------------------------------------------------------------
     */


    /**
     * Тест: Успешное получение информации о пользователе по токену
     * Проверяет, что когда передан валидный токен и пользователь существует в системе,
     * система возвращает корректную информацию о пользователе
     */
    @Test
    void shouldReturnUserInfoWhenTokenIsValid() {
        // Подготовка тестовых данных
        String validToken = "valid.jwt.token";

        // Настройка поведения мок-объектов:
        // - При извлечении пользователя из токена возвращаем тестового пользователя
        // - При поиске пользователя в репозитории по ID возвращаем существующего пользователя
        when(userService.getUsernameFromToken(validToken)).thenReturn(user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Вызов тестируемого метода - получение информации о пользователе по токену
        UserDetails result = userService.getUser(validToken);

        // Проверки результатов:
        // - Убеждаемся, что результат не null
        // - Сравниваем все поля возвращенного DTO с ожидаемыми значениями
        assertNotNull(result);
        assertEquals(userDetails.getUserId(), result.getUserId());
        assertEquals(userDetails.getUsername(), result.getUsername());
        assertEquals(userDetails.getEmail(), result.getEmail());
        assertEquals(userDetails.getCreatedAt(), result.getCreatedAt());

        // Проверка вызовов зависимостей:
        // - Убеждаемся, что извлечение пользователя из токена было вызвано один раз
        // - Убеждаемся, что поиск пользователя в репозитории по ID был выполнен
        verify(userService, times(1)).getUsernameFromToken(validToken);
        verify(userRepository, times(1)).findById(userId);
    }

    /**
     * Тест: Ошибка при получении информации о несуществующем пользователе
     * Проверяет, что система выбрасывает исключение NotFoundException,
     * когда пользователь из токена не найден в базе данных
     */
    @Test
    void shouldThrowExceptionWhenUserNotFoundInDatabase() {
        // Подготовка тестовых данных
        String validToken = "valid.jwt.token";

        // Настройка поведения мок-объектов:
        // - При извлечении пользователя из токена возвращаем пользователя
        // - При поиске пользователя в репозитории возвращаем пустой Optional (пользователь не найден)
        when(userService.getUsernameFromToken(validToken)).thenReturn(user);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Проверяем, что при попытке получения информации выбрасывается исключение "не найден"
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userServiceImpl.getUser(validToken)
        );

        // Проверяем сообщение об ошибке
        assertEquals("Пользователь c id: " + userId + " не найден в системе",
                exception.getMessage());

        // Проверка вызовов зависимостей:
        // - Убеждаемся, что извлечение пользователя из токена было выполнено
        // - Убеждаемся, что поиск в репозитории был выполнен
        verify(userService, times(1)).getUsernameFromToken(validToken);
        verify(userRepository, times(1)).findById(userId);
    }

    /* -------------------------------------------------------------------------
     * -------------------------------------------------------------------------
     *  userService.updateUser
     * -------------------------------------------------------------------------
     * -------------------------------------------------------------------------
     */

    /**
     * Тест: Успешное обновление информации о пользователе
     * Проверяет, что когда передан валидный токен и новые данные пользователя уникальны,
     * система корректно обновляет информацию и возвращает обновленные данные
     */
    @Test
    void shouldUpdateUserSuccessfully() {
        // Подготовка тестовых данных
        String validToken = "valid.jwt.token";
        UpdateUserRequestDto updateRequest = new UpdateUserRequestDto("john_updated", "john_updated@example.com");

        // Настройка поведения мок-объектов:
        // - При извлечении пользователя из токена возвращаем тестового пользователя
        // - При поиске пользователя в репозитории возвращаем существующего пользователя
        // - При сохранении пользователя возвращаем обновленную версию
        when(userService.getUsernameFromToken(validToken)).thenReturn(user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Вызов тестируемого метода - обновление информации о пользователе
        UpdateUserResponseDto result = userServiceImpl.updateUser(updateRequest, validToken);

        // Проверки результатов:
        // - Убеждаемся, что результат не null
        // - Проверяем, что метод завершился без исключений
        assertNotNull(result);

        // Проверка вызовов зависимостей:
        // - Убеждаемся, что извлечение пользователя из токена было выполнено
        // - Убеждаемся, что поиск пользователя в репозитории был выполнен
        // - Убеждаемся, что проверка уникальности данных была выполнена
        // - Убеждаемся, что сохранение пользователя было выполнено
        verify(userService, times(1)).getUsernameFromToken(validToken);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findAll();
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Тест: Ошибка при обновлении пользователя с уже существующим email
     * Проверяет, что система выбрасывает исключение DataConflictException,
     * когда пытаются обновить email на уже существующий в системе
     */
    @Test
    void shouldThrowExceptionWhenUpdatedEmailAlreadyExists() {
        // Подготовка тестовых данных
        String validToken = "valid.jwt.token";
        UpdateUserRequestDto updateRequest = new UpdateUserRequestDto("john_doe", "existing@example.com");

        // Создаем существующего пользователя с email, на который пытаемся поменять
        User existingUser = new User(
                UUID.randomUUID(),
                "other_user",
                "existing@example.com", // Email, который уже используется
                "Password123",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                List.of(role)
        );

        // Настройка поведения мок-объектов:
        // - При извлечении пользователя из токена возвращаем тестового пользователя
        // - При поиске пользователя в репозитории возвращаем существующего пользователя
        // - При запросе всех пользователей возвращаем список с конфликтующим пользователем
        when(userService.getUsernameFromToken(validToken)).thenReturn(user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(existingUser));

        // Проверяем, что при попытке обновления выбрасывается исключение
        DuplicateUserException exception = assertThrows(
                DuplicateUserException.class,
                () -> userServiceImpl.updateUser(updateRequest, validToken)
        );

        // Проверяем сообщение об ошибке
        assertEquals("Email existing@example.com уже используется в системе другим пользователем.",
                exception.getMessage());

        // Проверка, что сохранение не было выполнено при конфликте:
        // - Убеждаемся, что пользователь не был сохранен в базу
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Тест: Ошибка при обновлении несуществующего пользователя
     * Проверяет, что система выбрасывает исключение NotFoundException,
     * когда пользователь из токена не найден в базе данных
     */
    @Test
    void shouldThrowExceptionWhenUserToUpdateNotFound() {
        // Подготовка тестовых данных
        String validToken = "valid.jwt.token";
        UpdateUserRequestDto updateRequest = new UpdateUserRequestDto("john_updated", "john_updated@example.com");

        // Настройка поведения мок-объектов:
        // - При извлечении пользователя из токена возвращаем пользователя
        // - При поиске пользователя в репозитории возвращаем пустой Optional (пользователь не найден)
        when(userService.getUsernameFromToken(validToken)).thenReturn(user);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Проверяем, что при попытке обновления выбрасывается исключение "не найден"
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userServiceImpl.updateUser(updateRequest, validToken)
        );

        // Проверяем сообщение об ошибке - должно содержать ID пользователя
        assertEquals("Пользователь c id: " + userId + " не найден в системе",
                exception.getMessage());

        // Проверка, что операции не были выполнены:
        // - Сохранение пользователя не должно происходить
        // - Проверка уникальности не должна выполняться
        verify(userRepository, never()).save(any(User.class));
        verify(userRepository, never()).findAll();
    }


}