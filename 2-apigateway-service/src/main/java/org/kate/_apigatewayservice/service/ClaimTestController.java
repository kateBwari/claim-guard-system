package org.kate._apigatewayservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.kate._apigatewayservice.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/test")
public class ClaimTestController {

    @Autowired
    private ClaimServiceClient claimServiceClient;

    // Move the Circuit Breaker here for testing
    @CircuitBreaker(name = "claimServiceCircuit", fallbackMethod = "fallbackForClaimService")
    @GetMapping("/claim/{id}")
    public Mono<ApiResponse> testCircuit(@PathVariable("id") Long id) {
        return claimServiceClient.getClaimDetails(id);
    }

    public Mono<ApiResponse> fallbackForClaimService(Long id, Throwable t) {
        return Mono.just(new ApiResponse(false, "Controller Fallback: Claim Service is down", t.getMessage()));
    }
}