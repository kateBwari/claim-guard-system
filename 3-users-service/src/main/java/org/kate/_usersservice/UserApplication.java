package org.kate._usersservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.kate._usersservice.repository") // Add this!
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run( UserApplication.class, args);
    }
}