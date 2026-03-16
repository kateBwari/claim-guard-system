package org.kate._apigatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient // Connects the gateway to your Service Discovery service
public class ApiGateWay {

    public static void main(String[] args) {
        // Starts the Netty-based Gateway server
        SpringApplication.run(ApiGateWay.class, args);
    }
}