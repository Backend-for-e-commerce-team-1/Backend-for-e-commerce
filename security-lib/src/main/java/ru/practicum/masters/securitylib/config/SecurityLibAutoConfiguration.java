package ru.practicum.masters.securitylib.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.masters.securitylib.filter.JwtAuthenticationFilter;
import ru.practicum.masters.securitylib.service.ExcludeSecurityService;
import ru.practicum.masters.securitylib.service.JwtService;
import ru.practicum.masters.securitylib.service.SecurityContextService;

@Configuration
@EnableConfigurationProperties(ru.practicum.masters.securitylib.config.SecurityLibProperties.class)
@ConditionalOnClass({JwtService.class, ExcludeSecurityService.class, JwtAuthenticationFilter.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityLibAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JwtService jwtService(SecurityLibProperties properties) {
        return new JwtService(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public ExcludeSecurityService excludeSecurityService(SecurityLibProperties properties) {
        return new ExcludeSecurityService(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityContextService securityContextService() {
        return new SecurityContextService();
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtAuthenticationFilter jwtAuthenticationFilter(SecurityLibProperties properties) {
        return new JwtAuthenticationFilter(excludeSecurityService(properties), jwtService(properties));
    }
}
