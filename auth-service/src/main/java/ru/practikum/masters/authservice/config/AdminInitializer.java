package ru.practikum.masters.authservice.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import ru.practikum.masters.authservice.service.AdminService;

@Slf4j
@RequiredArgsConstructor
public class AdminInitializer {

    private final AdminService adminService;

    @Value("${app.admin.auto-create}")
    boolean autoCreate;

    @Value("${app.admin.password}")
    private String adminPassword;

    @PostConstruct
    public void initAdmin() {
        if (autoCreate && adminPassword != null && !adminPassword.isBlank()) {
            log.info("Попытка создания администратора системы...");
            adminService.addAdminUserIfNotExists(adminPassword);
        } else if (!autoCreate) {
            log.info("Автоматическое создание администратора отключено.");
        }
    }

}
