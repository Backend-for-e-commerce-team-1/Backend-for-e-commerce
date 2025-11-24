package ru.practikum.masters.securitylib.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
import ru.practikum.masters.securitylib.service.JwtService;
import ru.practikum.masters.securitylib.service.ExcludeSecurityService;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final int BEARER_PREFIX_LENGTH = "Bearer ".length();
    private final ExcludeSecurityService excludeSecurityService;
    private final JwtService jwtService;


    @Autowired
    public JwtAuthenticationFilter(ExcludeSecurityService excludeSecurityService, JwtService jwtService) {
        this.excludeSecurityService = excludeSecurityService;
        this.jwtService = jwtService;
    }


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Пропускаем публичные эндпоинты
        final String path = request.getServletPath();
        if (excludeSecurityService.isPublicEndpoint(path)) {
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

            // Извлекаем cвойства пользователя из токена
            var claims = jwtService.validateToken(jwt);

            // Создаем объект аутентификации для Spring Security
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    claims,
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
}