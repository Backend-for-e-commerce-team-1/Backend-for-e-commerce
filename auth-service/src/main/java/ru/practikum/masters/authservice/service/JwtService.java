package ru.practikum.masters.authservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practikum.masters.authservice.model.User;


import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
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
     *
     * @param token - токен
     * @return - Claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}
