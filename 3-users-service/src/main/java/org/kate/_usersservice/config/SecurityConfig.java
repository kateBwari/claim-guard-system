package org.kate._usersservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
    @EnableWebSecurity
    @EnableMethodSecurity // Allows us to use @PreAuthorize for specific policies
    public class  SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(csrf -> csrf.disable()) // Disable for APIs
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/user/**").permitAll()
                            .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                            .anyRequest().authenticated() // Everything else needs a JWT
                    )
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .oauth2ResourceServer(oauth2 -> oauth2
                            .jwt(jwt -> jwt.
                                    decoder(jwtDecoder())
                                    .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                    )
                    .build();
        }


        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {
            return new JwtAuthenticationConverter(); // This refers to the class you just finished
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
            return config.getAuthenticationManager();

        }

    @Bean
    public JwtDecoder jwtDecoder() {
        String secret = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

        // This is the correct one that matches your Auth Service
        byte[] decodedKey = java.util.Base64.getDecoder().decode(secret);

        // Make sure you use 'decodedKey' here, NOT 'keyBytes'
        SecretKeySpec secretKey = new SecretKeySpec(decodedKey, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
    }