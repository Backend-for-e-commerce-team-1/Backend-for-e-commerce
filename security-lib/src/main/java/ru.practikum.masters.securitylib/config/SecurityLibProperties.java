package ru.practikum.masters.securitylib.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "securitylibproperties")
public class SecurityLibProperties {

    @Value("${spring.security.jwt.secretKey}")
    private String secret;

    @Value("${spring.security.jwt.expiration}")
    private Long expiration;

    @Value("${spring.security.public-endpoints}")
    private String[] publicEndpoints;

}
