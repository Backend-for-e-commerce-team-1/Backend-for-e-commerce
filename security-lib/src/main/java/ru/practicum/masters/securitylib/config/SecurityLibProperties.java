package ru.practicum.masters.securitylib.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "app.security")
public class SecurityLibProperties {

    private String jwtSecret;

    private Long jwtExpiration;

    private String[] publicEndpoints;

}
