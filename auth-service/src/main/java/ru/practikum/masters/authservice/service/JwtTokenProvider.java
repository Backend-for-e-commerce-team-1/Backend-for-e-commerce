package ru.practikum.masters.authservice.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.data.DefaultRepositoryTagsProvider;
import org.springframework.stereotype.Service;
import ru.practikum.masters.authservice.exception.InvalidCredentialsException;
import ru.practikum.masters.authservice.model.Role;
import ru.practikum.masters.authservice.model.RoleType;
import ru.practikum.masters.authservice.model.User;
import ru.practikum.masters.authservice.repository.RoleRepository;

import javax.crypto.SecretKey;
import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final RoleRepository roleRepository;

    private final DefaultRepositoryTagsProvider repositoryTagsProvider;

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
        claims.put("roles", user.getRoles());

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

        //Извлечение списка ролей
        List<?> roleList = validateToken(token).get("roles", List.class);
        List<Role> roles = convertToRoleList(roleList);
        user.setRoles(roles);

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

    /**
     * Конвертация списка ролей из токена в коллекцию объектов Role
     *
     * @param rawList List
     * @return List role
     */
    private List<Role> convertToRoleList(List<?> rawList) {
        Role role = new Role();
        if (rawList == null) {
            return Collections.emptyList();
        }

        List<Role> roles = new ArrayList<>();
        for (Object item : rawList) {
            if (item instanceof String) {
                role.setRoleName(RoleType.valueOf((String) item));
                roles.add(role);
            } else if (item instanceof Role) {
                roles.add((Role) item);
            }
        }
        return (roles);
    }

}
