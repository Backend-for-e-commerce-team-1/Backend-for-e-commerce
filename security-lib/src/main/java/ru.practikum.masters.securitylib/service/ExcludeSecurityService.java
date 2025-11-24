package ru.practikum.masters.securitylib.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import ru.practikum.masters.securitylib.config.SecurityLibProperties;

import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcludeSecurityService {

    private final SecurityLibProperties properties;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    /**
     * Является ли endpoint публичным
     * Список публичных endpoints лежит в файле application.yaml
     *
     * @param path String
     * @return boolean
     */
    public boolean isPublicEndpoint(String path) {
        return Arrays.stream(properties.getPublicEndpoints())
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
