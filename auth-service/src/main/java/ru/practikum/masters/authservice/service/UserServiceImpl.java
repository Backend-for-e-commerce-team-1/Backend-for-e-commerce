package ru.practikum.masters.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Регистрация пользователя в системе
     *
     * @param newUser новый пользователь
     * @return UserDto пользователь
     */
    @Override
    @Transactional
    public RegisterResponse registerUser(RegisterRequest newUser) {
        User user = userMapper.toUserFromNewUser(newUser);
        //Проверяем уникальность. Если нарушена - выбрасываем исключение
        emailUsageCheck(user.getEmail(), user.getUsername(), UUID.randomUUID());
        //Хэшируем пароль
        String hashedPassword = passwordEncoder.encode(newUser.getPassword());
        user.setPassword(hashedPassword);
        //Назначаем роль
        Role role = getRole(RoleType.ROLE_USER);
        user.setRoles(List.of(role));
        //Устанавливаем дату создания
        user.setCreatedAt(LocalDateTime.now());
        //Сохраняем в базе
        user = userRepository.save(user);
        //Возвращаем нового пользователя
        return userMapper.toUserDto(user);
    }

    /**
     * Аутентификация пользователя в системе
     *
     * @param authUserDto Email и пароль
     * @return AuthUserResponseDto Токен и время его действия
     */
    @Override
    public LoginResponse authenticate(LoginRequest authUserDto) {
        User user = authenticate(authUserDto.getEmail(), authUserDto.getPassword());

        // Генерируем JWT токен
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId().toString());
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());

        String token = jwtTokenProvider.generateToken(claims, user.getEmail());
        Long expiresIn = jwtTokenProvider.getExpirationInSeconds();

        // Собираем ответ
        LoginResponse authUserResponseDto = new LoginResponse();
        authUserResponseDto.setToken(token);
        authUserResponseDto.setExpiresIn(expiresIn);

        return authUserResponseDto;
    }

    /**
     * Информация о пользователе по его токену.
     * В процессе токен валидируется.
     *
     * @param token токен
     * @return UserDto  пользователь
     */
    @Override
    public UserDetails getUser(String token) {
        User userFromToken = jwtTokenProvider.getUsernameFromToken(token);
        User user = userRepository.findById(userFromToken.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь c id: " + userFromToken.getUserId()
                        + " не найден в системе"));
        return userMapper.toUserDtoDetails(user);
    }

    /**
     * Внесение изменений в пользователя
     *
     * @param updateUser что будет меняться
     * @param token      токен
     * @return UpdateUserResponseDto исправленный пользователь
     */
    @Transactional
    @Override
    public UpdateUserResponseDto updateUser(UpdateUserRequestDto updateUser, String token) {
        //Извлекаем пользователя из токена
        User userFromToken = getUsernameFromToken(token);

        //Находим пользователя в базе
        User oldUser = userRepository.findById(userFromToken.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь c id: " + userFromToken.getUserId()
                        + " не найден в системе"));

        //Проверяем уникальность новых данных. Если нарушена - выбрасываем исключение
        emailUsageCheck(updateUser.getEmail(), updateUser.getUsername(), oldUser.getUserId());

        // Обновляем данные пользователя из DTO
        userMapper.updateUserFromDto(updateUser, oldUser);
        LocalDateTime updateAt = LocalDateTime.now(); // Дата и время обновления пользователя
        oldUser.setUpdatedAt(updateAt);
        // Сохраняем обновленного пользователя
        User updatedUser = userRepository.save(oldUser);
        UpdateUserResponseDto updateUserResponseDto = userMapper.toUpdateResponseFromUser(updatedUser);
        updateUserResponseDto.setUpdatedAt(updateAt);
        return updateUserResponseDto;
    }

    /**
     * Проверяем токен и если он валидный,
     * извлекаем информацию о пользователе
     *
     * @param token - string
     * @return - User
     */
    public User getUsernameFromToken(String token) {
        User user = new User();
        var claims = jwtTokenProvider.validateToken(token);
        user.setUserId(UUID.fromString(claims.get("userId", String.class)));
        user.setEmail(claims.get("email", String.class));
        user.setUsername(claims.get("username", String.class));

        return user;
    }

    /**
     * Проверяем уникальность имени пользователя и эл.почты
     *
     * @param email    - электронная почта пользователя
     * @param username - имя пользователя
     * @param userId   - id пользователя нужен для update пользователя,
     *                 что бы не проверять на уникальность самого себя
     */
    private void emailUsageCheck(String email, String username, UUID userId) {
        List<User> users = new ArrayList<>(userRepository.findAll());
        for (User user : users) {
            if (!userId.equals(user.getUserId()) && user.getEmail().equals(email)) {
                log.error("Email {} уже используется в системе другим пользователем.", email);
                throw new DuplicateUserException("Email " + email + " уже используется в системе другим пользователем.");
            }
            if (!userId.equals(user.getUserId()) && user.getUsername().equals(username)) {
                log.error("Имя пользователя {} уже используется в системе другим пользователем.", username);
                throw new DuplicateUserException("Имя пользователя " + username + " уже используется в системе другим пользователем.");
            }
        }
    }

    /**
     * Находим в базе пользователя по email.
     * Сверяем пароль из базы с паролем из входящего сообщения.
     * Если совпадает, возвращаем пользователя.
     * Иначе выбрасываем ошибку AuthenticationException
     *
     * @param email    - почта
     * @param password - пароль
     * @return User - пользователь
     */
    private User authenticate(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user.get();
        } else {
            log.error("Неверное имя пользователя или пароль.");
            throw new AuthenticationException("Неверное имя пользователя или пароль.");
        }
    }

    private Role getRole(RoleType role) {
        return roleRepository.findByRoleName(role)
                .orElseThrow(() -> new NotFoundException("Роль  '" + role + "' не найдена в системе."));
    }

}
