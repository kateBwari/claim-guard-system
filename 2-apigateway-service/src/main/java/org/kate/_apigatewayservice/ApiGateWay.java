package org.kate._apigatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@EnableDiscoveryClient // Connects the gateway to your Service Discovery service
public class ApiGateWay {

    public static void main(String[] args) {
        // Starts the Netty-based Gateway server
        SpringApplication.run(ApiGateWay.class, args);
    }
    @Bean
        @LoadBalanced // This is crucial for service discovery via Eureka
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();

        }
    }
