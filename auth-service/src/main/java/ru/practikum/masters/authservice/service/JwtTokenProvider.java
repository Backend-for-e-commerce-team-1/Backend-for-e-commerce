package ru.practikum.masters.authservice.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practikum.masters.authservice.exception.InvalidCredentialsException;
import ru.practikum.masters.authservice.model.User;


import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
@Slf4j
public class JwtTokenProvider {

    @Value("${spring.security.jwt.secretKey}")
    private String secret;

    @Value("${spring.security.jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId().toString());
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    public Long getExpirationInSeconds() {
        return expiration;
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
        user.setUserId(UUID.fromString(validateToken(token).get("userId", String.class)));
        user.setEmail(validateToken(token).get("email", String.class));
        user.setUsername(validateToken(token).get("username", String.class));

        return user;
    }

    /**
     * Метод для валидации и парсинга токена
     * В случае возникновения ошибок выбрасываем исключения
     *
     * @param token - токен
     * @return - Claims
     */
    private Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (MalformedJwtException e) {
            log.error("Невалидный формат JWT токена: {}", token);
            throw new InvalidCredentialsException("Невалидный формат токена", "TOKEN_INVALID_FORMAT");

        } catch (ExpiredJwtException e) {
            log.error("JWT токен истек: {}", token);
            throw new InvalidCredentialsException("Токен истек", "TOKEN_EXPIRED");

        } catch (SecurityException e) {
            log.error("Ошибка подписи JWT токена: {}", token);
            throw new InvalidCredentialsException("Неверная подпись токена", "TOKEN_INVALID_SIGNATURE");

        } catch (JwtException e) {
            log.error("Ошибка парсинга JWT токена: {}", e.getMessage());
            throw new InvalidCredentialsException("Ошибка валидации токена", "TOKEN_VALIDATION_ERROR");

        } catch (IllegalArgumentException e) {
            log.error("Пустой или null токен");
            throw new InvalidCredentialsException("Токен не может быть пустым", "TOKEN_EMPTY");
        }
    }
}
