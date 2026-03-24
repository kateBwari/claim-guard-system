package org.kate.claimservice.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.kate.claimservice.ClaimProducer;
import org.kate.claimservice.model.ApiResponse;
import org.kate.claimservice.model.Claim;
import org.kate.claimservice.model.ClaimDTO;
import org.kate.claimservice.repository.ClaimRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;


import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimRepository repository;
    private final ClaimProducer claimProducer;


    // Proper Constructor Injection
    public ClaimController(ClaimRepository repository, RestTemplate restTemplate, ClaimProducer claimProducer) {
        this.repository = repository;
        this.claimProducer = claimProducer;

    }


    @PostMapping("/create")
    @CircuitBreaker(name = "claimServiceCircuit", fallbackMethod = "fallbackForCreateClaim")
    public ResponseEntity<ApiResponse> createClaim(
            @RequestBody ClaimDTO claimDTO,
            @AuthenticationPrincipal Jwt jwt
    ) {
        // 1. Extract Identity from the Token
        // authentication.getName() typically returns the username/subject from the JWT
        String actualUsername = jwt.getSubject();

        // Note: If you need a specific numeric Long ID from the token,
        // you would usually extract it from the claims.
        // For now, we'll use the authenticated name.

        // 2. Check for existing claim
        if (repository.existsByPolicyNumberAndDescription(claimDTO.getPolicyNumber(), claimDTO.getDescription())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, "Claim already exists for this policy and description", null));
        }

        // 3. Map DTO to Entity
        Claim claim = new Claim();
        claim.setClaimNumber(generateClaimNumber());
        claim.setDescription(claimDTO.getDescription());
        claim.setAmount(claimDTO.getAmount());
        claim.setPolicyNumber(claimDTO.getPolicyNumber());
        claim.setStatus("PENDING");

        // Set user details extracted from the token
        claim.setUsername(actualUsername);

        // If your Claim entity requires a Long ID and it's stored in the token's principal
        // claim.setUserId(userIdFromToken);

        // 4. Save and return
        Claim saved = repository.save(claim);
        claimProducer.sendToQueue(saved);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Claim created successfully", saved));
    }
    public ResponseEntity<ApiResponse> fallbackForCreateClaim(ClaimDTO claimDTO, Throwable t) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiResponse(false, "Claim Service is temporarily busy. Please try again later.", t.getMessage()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse> deleteClaim(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok(new ApiResponse(true, "Claim deleted successfully", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, "Claim not found", null));
    }

    // Generate unique claim number
    private String generateClaimNumber() {
        String prefix = "CLM";
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(7);
        int randomNum = (int) (Math.random() * 900) + 100;
        return prefix + "-" + timestamp + "-" + randomNum;
    }
}