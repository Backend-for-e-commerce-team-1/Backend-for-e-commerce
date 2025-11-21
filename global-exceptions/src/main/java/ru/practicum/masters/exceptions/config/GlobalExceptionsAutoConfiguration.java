package ru.practicum.masters.exceptions.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.masters.exceptions.GlobalExceptionHandler;

@Configuration
@ConditionalOnClass(GlobalExceptionHandler.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(GlobalExceptionsProperties.class)
@RequiredArgsConstructor
public class GlobalExceptionsAutoConfiguration {

    private final GlobalExceptionsProperties exceptionsProperties;

    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler(exceptionsProperties);
    }
}
