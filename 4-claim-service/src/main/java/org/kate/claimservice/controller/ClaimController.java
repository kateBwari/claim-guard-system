package org.kate.claimservice.controller;

import org.kate.claimservice.model.ApiResponse;
import org.kate.claimservice.model.Claim;
import org.kate.claimservice.model.ClaimDTO;
import org.kate.claimservice.repository.ClaimRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimRepository repository;
    private final RestTemplate restTemplate;

    // Proper Constructor Injection
    public ClaimController(ClaimRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createClaim(
            @RequestBody ClaimDTO claimDTO,
            @RequestHeader("X-Auth-User-Id") Long userId
    ) {
        if (repository.existsByPolicyNumberAndDescription(claimDTO.getPolicyNumber(), claimDTO.getDescription())) {
            return ResponseEntity.status(HttpStatus.CONFLICT) // 409 Conflict
                    .body(new ApiResponse(false, "Claim already exists for this policy and description", null));
        }
        // 1. Fetch the real username dynamically from User Service
        String userServiceUrl = "http://user-service/user/" + userId + "/username";
        String actualUsername;
        try {
            actualUsername = restTemplate.getForObject(userServiceUrl, String.class);
        } catch (Exception e) {
            actualUsername = "Unknown User"; // Fallback if service is down
        }

        // 2. Map DTO to Entity
        Claim claim = new Claim();
        claim.setClaimNumber(generateClaimNumber()); // Generate unique CLM number
        claim.setDescription(claimDTO.getDescription());
        claim.setAmount(claimDTO.getAmount());
        claim.setStatus("PENDING");
        claim.setUserId(userId);
        claim.setPolicyNumber(claimDTO.getPolicyNumber());
        claim.setUsername(actualUsername);

        // 3. Save and return the full object
        Claim saved = repository.save(claim);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Claim created successfully", saved));
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