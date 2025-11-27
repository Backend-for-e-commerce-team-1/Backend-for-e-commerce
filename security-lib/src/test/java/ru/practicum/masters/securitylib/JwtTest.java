//package ru.practicum.masters.securitylib;
//
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import ru.practicum.masters.securitylib.filter.JwtAuthenticationFilter;
//import ru.practicum.masters.securitylib.service.ExcludeSecurityService;
//import ru.practicum.masters.securitylib.service.JwtService;
//
////@Import(JwtTest.TestConfig.class)
//public class JwtTest {
//
//    @Nested
//    @TestConfiguration
//    class TestConfig {
//        @Bean
//        public JwtService jwtService() {
//            return new JwtService(properties);
//        }
//
//        @Bean
//        public ExcludeSecurityService excludeSecurityService() {
//            return new ExcludeSecurityService(properties);
//        }
//
//        @Bean
//        public JwtAuthenticationFilter jwtAuthenticationFilter() {
//            return new JwtAuthenticationFilter(excludeSecurityService(properties), jwtService(properties));
//        }
//    }
//
//    @Test
//    void testJwtService() {
//    }
//
//    @Test
//    void testExcludeSecurityService() {
//    }
//
//    @Test
//    void testJwtAuthenticationFilter() {
//    }
//}
