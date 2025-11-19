package ru.practikum.masters.authservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.practikum.masters.authservice.model.User;
import ru.practikum.masters.authservice.service.JwtTokenProvider;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final int BEARER_PREFIX_LENGTH = "Bearer ".length();
    private final JwtTokenProvider jwtTokenProvider;

    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${spring.security.public-endpoints}")
    private String[] publicEndpoints;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String path = request.getServletPath();

        // Пропускаем публичные эндпоинты
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        // Если нет заголовка Authorization или он не начинается с "Bearer ", возвращаем 401
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }

        try {
            // Извлекаем токен (убираем "Bearer ")
            String jwt = authHeader.substring(BEARER_PREFIX_LENGTH);

            // Извлекаем пользователя из токена
            User user = jwtTokenProvider.getUsernameFromToken(jwt);

            // Создаем объект аутентификации для Spring Security
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Устанавливаем аутентификацию в контекст Security
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (Exception e) {
            // Если токен невалиден, очищаем контекст безопасности
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT token: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Является ли endpoint публичным
     * Список публичных endpoints лежит в файле application.yaml
     *
     * @param path String
     * @return boolean
     */
    private boolean isPublicEndpoint(String path) {
        return Arrays.stream(publicEndpoints)
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

}