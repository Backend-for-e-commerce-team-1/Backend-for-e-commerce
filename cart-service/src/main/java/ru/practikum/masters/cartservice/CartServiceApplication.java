package ru.practikum.masters.cartservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class CartServiceApplication {
    static void main(String[] args) {
        SpringApplication.run(CartServiceApplication.class, args);
    }
}