package ru.practicum.masters.securitylib.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SecurityContextService {

    public UUID getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null) {
            return UUID.fromString((String) ((Map<String, Object>) principal).get("userId"));
        }
        throw new UnsupportedOperationException();
    }
}
