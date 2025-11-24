//package ru.practikum.masters.authservice;
//
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Import;
//import org.springframework.mock.web.MockHttpServletRequest;
//import ru.practikum.masters.securitylib.config.SecurityLibProperties;
//import ru.practikum.masters.securitylib.filter.JwtAuthenticationFilter;
//import ru.practikum.masters.securitylib.service.ExcludeSecurityService;
//import ru.practikum.masters.securitylib.service.JwtService;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@SpringBootTest
//@Import(JwtTest.TestConfig.class)
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
//        // given
//        ResourceNotFoundException exception = new ResourceNotFoundException("Ресурс не найден");
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.setRequestURI("/someUrl");
//
//        // when
//        GlobalExceptionHandler handler = new GlobalExceptionHandler(new GlobalExceptionsProperties());
//        ErrorResponse response = handler.handleResourceNotFoundException(exception, request);
//
//        // then
//        assertNotNull(response);
//        assertEquals("NOT_FOUND", response.getStatus());
//        assertEquals("Resource not found", response.getError());
//        assertEquals("Ресурс не найден", response.getMessage());
//        assertEquals("/someUrl", response.getPath());
//
//        System.out.println("ResourceNotFoundException обрабатывается корректно!");
//    }
//
//    @Test
//    void testExcludeSecurityService() {
//        // given
//        DuplicateResourceException exception = new DuplicateResourceException("Дубликат ресурса");
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.setRequestURI("/someUrl");
//
//        // when
//        GlobalExceptionHandler handler = new GlobalExceptionHandler(new GlobalExceptionsProperties());
//        ErrorResponse response = handler.handleDuplicateResourceException(exception, request);
//
//        // then
//        assertNotNull(response);
//        assertEquals("CONFLICT", response.getStatus());
//        assertEquals("Resource already exists", response.getError());
//        assertEquals("Дубликат ресурса", response.getMessage());
//        assertEquals("/someUrl", response.getPath());
//
//        System.out.println("DuplicateResourceException обрабатывается корректно!");
//    }
//
//    @Test
//    void testJwtAuthenticationFilter() {
//        // given
//        DuplicateResourceException exception = new DuplicateResourceException("Дубликат ресурса");
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.setRequestURI("/someUrl");
//
//        // when
//        GlobalExceptionHandler handler = new GlobalExceptionHandler(new GlobalExceptionsProperties());
//        ErrorResponse response = handler.handleDuplicateResourceException(exception, request);
//
//        // then
//        assertNotNull(response);
//        assertEquals("CONFLICT", response.getStatus());
//        assertEquals("Resource already exists", response.getError());
//        assertEquals("Дубликат ресурса", response.getMessage());
//        assertEquals("/someUrl", response.getPath());
//
//        System.out.println("DuplicateResourceException обрабатывается корректно!");
//    }
//}
