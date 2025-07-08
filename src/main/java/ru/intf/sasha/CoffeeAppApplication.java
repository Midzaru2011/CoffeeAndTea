package ru.intf.sasha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoffeeAppApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(CoffeeAppApplication.class);
    }

    public static void main(String[] args) {
        // Для запуска в режиме standalone (например, при тестировании локально)
        SpringApplication.run(CoffeeAppApplication.class, args);
    }
}