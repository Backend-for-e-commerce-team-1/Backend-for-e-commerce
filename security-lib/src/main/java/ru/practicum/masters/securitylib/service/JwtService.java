package ru.practicum.masters.securitylib.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.masters.securitylib.config.SecurityLibProperties;
import ru.practicum.masters.securitylib.exceptions.InvalidCredentialsException;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final SecurityLibProperties properties;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(properties.getJwtSecret().getBytes());
    }

    public String generateToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + properties.getJwtExpiration() * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    public Long getExpirationInSeconds() {
        return properties.getJwtExpiration();
    }

    /**
     * Метод для валидации и парсинга токена
     * В случае возникновения ошибок выбрасываем исключения
     *
     * @param token - токен
     * @return - Claims
     */
    public Claims validateToken(String token) {
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
