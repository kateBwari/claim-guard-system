package org.kate._usersservice.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
    public class FeignConfig {

        @Bean
        public RequestInterceptor requestInterceptor() {
            return requestTemplate -> {
                // 1. Grab the current request's attributes
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if (attributes != null) {
                    // 2. Extract the "Authorization" header (the JWT)
                    String authToken = attributes.getRequest().getHeader("Authorization");

                    // 3. Attach it to the outgoing Feign request
                    if (authToken != null) {
                        requestTemplate.header("Authorization", authToken);
                    }
                }
            };
        }
    }

