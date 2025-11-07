package ru.practikum.masters.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practikum.masters.authservice.dto.*;
import ru.practikum.masters.authservice.exception.AuthenticationException;
import ru.practikum.masters.authservice.exception.DataConflictException;
import ru.practikum.masters.authservice.mapper.UserMapper;
import ru.practikum.masters.authservice.model.User;
import ru.practikum.masters.authservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDto addUser(NewUserRequestDto newUser) {
        User user = UserMapper.toUserFromNewUser(newUser);
        //Проверяем уникальность. Если нарушена - выбрасываем исключения и откатываемся
        emailUsageCheck(user.getEmail(), user.getUsername(), UUID.randomUUID());
        //Хэшируем пароль
        String hashedPassword = passwordEncoder.encode(newUser.getPassword());
        user.setPassword(hashedPassword);
        //Устанавливаем дату создания
        user.setCreatedAt(LocalDateTime.now());
        //Сохраняем в базе
        user = userRepository.save(user);
        //Возвращаем нового пользователя
        return UserMapper.toUserDto(user);
    }

    @Override
    public AuthUserResponseDto authUser(AuthUserRequestDto authUserDto) {
        User user = authenticate(authUserDto.getEmail(), authUserDto.getPassword());

        // Генерируем JWT токен
        String token = jwtService.generateToken(user);
        Long expiresIn = jwtService.getExpirationInSeconds();

        // Собираем ответ
        AuthUserResponseDto authUserResponseDto = new AuthUserResponseDto();
        authUserResponseDto.setToken(token);
        authUserResponseDto.setExpires_in(expiresIn);

        return authUserResponseDto;
    }

    @Override
    public UserDto getUser(String token) {
        return null;
    }

    @Override
    public UpdateUserResponseDto updateUser(UpdateUserRequestDto updateUser, String token) {
        return null;
    }

    /**
     * Проверяем уникальность имени пользователя и эл.почты
     * @param email - электронная почта пользователя
     * @param username - имя пользователя
     * @param userId - id пользователя нужен для update пользователя,
     *               что бы не проверять на уникальность самого себя
     */
    private void emailUsageCheck(String email, String username, UUID userId) {
        List<User> users = new ArrayList<>(userRepository.findAll());
        for (User user : users) {
            if (!userId.equals(user.getUserId()) && user.getEmail().equals(email)) {
                log.error("Email {} уже используется в системе другим пользователем.", email);
                throw new DataConflictException("Email " + email + " уже используется в системе другим пользователем.");
            }
            if (!userId.equals(user.getUserId()) && user.getUsername().equals(username)) {
                log.error("Имя пользователя {} уже используется в системе другим пользователем.", username);
                throw new DataConflictException("Имя пользователя " + username + " уже используется в системе другим пользователем.");
            }
        }
    }

    /**
     * Находим в базе пользователя по email.
     * Сверяем пароль из базы с паролем из входящего сообщения.
     * Если совпадает, возвращаем пользователя.
     * Иначе выбрасываем ошибку AuthenticationException
     *
     * @param email - почта
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

}
