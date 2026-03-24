package org.kate._apigatewayservice.controller;

import org.kate._apigatewayservice.model.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    // This handles the GET requests (like your test)
    @GetMapping("/fallback/claims")
    public Mono<ApiResponse> getFallback() {
        return Mono.just(new ApiResponse(false, "Claim Service is down (GET)", null));
    }

    // THIS IS THE ONE YOU ARE MISSING:
    // It must be @PostMapping because your 'create' request is a POST
    @PostMapping("/fallback/claims")
    public Mono<ApiResponse> postFallback() {
        return Mono.just(new ApiResponse(false, "Claim Service is temporarily unavailable. Your claim could not be processed.", null));
    }
}
