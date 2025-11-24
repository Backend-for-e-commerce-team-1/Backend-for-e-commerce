package ru.practicum.masters.exceptions.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "global.exceptions")
@Getter
@Setter
public class GlobalExceptionsProperties {
    private boolean logExceptions = false;
}