package org.kate._apigatewayservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.kate._apigatewayservice.dto.ClaimDto;
import org.kate._apigatewayservice.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ClaimServiceClient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @CircuitBreaker(name = "claimServiceCircuit", fallbackMethod = "fallbackForClaimService")
    public Mono<ApiResponse> getClaimDetails(Long id) {
        return webClientBuilder.build()
                .get()
                .uri("http://claim-service/claims/" + id)
                .retrieve()
                .bodyToMono(ApiResponse.class);
    }
    public Mono<ApiResponse> fallbackForClaimService(ClaimDto dto, Throwable t) {
        String errorMessage = "Claim Service is currently unavailable for Claim ID: " + dto.getId();
        log.info("FALLBACK METHOD : {}",t.getMessage());
        return Mono.just(new ApiResponse(false, "System busy, claim not created", null));
    }
}