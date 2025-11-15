package ru.practikum.masters.authservice.service;

import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.practikum.masters.authservice.exception.NotFoundException;
import ru.practikum.masters.authservice.model.User;
import ru.practikum.masters.authservice.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.email}")
    private String adminEmail;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Проверка существования администратора в системе
     *
     * @return true/false
     */
    public boolean isAdminExists() {
        return userRepository.findByEmail(adminEmail).isPresent();
    }

    /**
     * Возвращает администратора системы
     *
     * @return User
     */
    public User getAdminUser() {
        return userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new NotFoundException("Администратор с email " + adminEmail
                        + " не найден в системе."));
    }

    /**
     * Создание администратора системы, если его нет в базе
     */
    public void addAdminUserIfNotExists(String password) {
        if (!isAdminExists()) {
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(password));
            adminUser.setCreatedAt(LocalDateTime.now());
            userRepository.save(adminUser);
            log.info("Создан администратор системы: {}.", adminEmail);
        } else {
            log.info("Администратор системы уже существует: {}.", adminEmail);
        }
    }

}
