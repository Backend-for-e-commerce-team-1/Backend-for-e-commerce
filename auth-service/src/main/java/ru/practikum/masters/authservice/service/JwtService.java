package ru.practikum.masters.authservice.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practikum.masters.authservice.exception.JwtValidationException;
import ru.practikum.masters.authservice.model.User;


import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
@Slf4j
public class JwtService {

    // TODO Наверное надо перенести в application.prop ?
    // TODO Или генерить рандомное значение?
    @Value("${jwt.secret:mySecretKey-c0382cfa-92ca-4c88-8c67-17f5e60dd71c-mySecretKey}")
    private String secret;

    // TODO Наверное надо перенести в application.prop ?
    @Value("${jwt.expiration:3600}")
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
    public User extractUserFromToken(String token) {
        User user = new User();
        user.setUserId(UUID.fromString(extractAllClaims(token).get("userId", String.class)));
        user.setEmail(extractAllClaims(token).get("email", String.class));
        user.setUsername(extractAllClaims(token).get("username", String.class));

        return user;
    }

    /**
     * Метод для валидации и парсинга токена
     * В случае возникновения ошибок выбрасываем исключения
     *
     * @param token - токен
     * @return - Claims
     */
    //TODO Вероятно валидацию надо перенести в контроллеры?
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (MalformedJwtException e) {
            log.error("Невалидный формат JWT токена: {}", token);
            throw new JwtValidationException("Невалидный формат токена", "TOKEN_INVALID_FORMAT");

        } catch (ExpiredJwtException e) {
            log.error("JWT токен истек: {}", token);
            throw new JwtValidationException("Токен истек", "TOKEN_EXPIRED");

        } catch (SecurityException e) {
            log.error("Ошибка подписи JWT токена: {}", token);
            throw new JwtValidationException("Неверная подпись токена", "TOKEN_INVALID_SIGNATURE");

        } catch (JwtException e) {
            log.error("Ошибка парсинга JWT токена: {}", e.getMessage());
            throw new JwtValidationException("Ошибка валидации токена", "TOKEN_VALIDATION_ERROR");

        } catch (IllegalArgumentException e) {
            log.error("Пустой или null токен");
            throw new JwtValidationException("Токен не может быть пустым", "TOKEN_EMPTY");
        }
    }
}
