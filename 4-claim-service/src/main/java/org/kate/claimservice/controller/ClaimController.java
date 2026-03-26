package org.kate.claimservice.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.kate.claimservice.ClaimProducer;
import org.kate.claimservice.model.ApiResponse;
import org.kate.claimservice.model.Claim;
import org.kate.claimservice.model.ClaimDTO;
import org.kate.claimservice.repository.ClaimRepository;
import org.kate.claimservice.service.ClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    @Autowired
    private ClaimService service;
    @Autowired
    private final ClaimRepository repository;
    private final ClaimProducer claimProducer;

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
        // authentication.getName() typically returns the username/subject from the JWT
        String actualUsername = jwt.getSubject();

        // 2. Check for existing claim
        if (repository.existsByPolicyNumberAndDescription(claimDTO.getPolicyNumber(), claimDTO.getDescription())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, "Claim already exists for this policy and description", null));
        }

        // 3. Map DTO to Entity
        Claim claim = new Claim();
        claim.setClaimNumber(generateClaimNumber());
        claim.setDescription(claimDTO.getDescription());
        claim.setBenefitType(claimDTO.getBenefitType());
        claim.setPolicyNumber(claimDTO.getPolicyNumber());
        claim.setStatus("PENDING");

        // Set user details extracted from the token
        claim.setUserIdentificationNumber(claimDTO.getUserIdentificationNumber());

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
        // Call the service and get the details back
        ClaimDTO deletedClaim = service.deleteClaim(id);

        String customMessage = "Claim #" + deletedClaim.getClaimNumber() +
                " belonging to User " + deletedClaim.getUserIdentificationNumber() +
                " has been deleted.";

        return ResponseEntity.ok(new ApiResponse(true, customMessage, deletedClaim));
    }

    @GetMapping("/my-claims/{idNumber}")
    public ResponseEntity<ApiResponse> getMyClaims(@PathVariable String idNumber) {
        List<Claim> claims = repository.findByUserIdentificationNumber(idNumber);

        if (claims.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "No claims found for this ID", null));
        }

        return ResponseEntity.ok(new ApiResponse(true, "Claims retrieved successfully", claims));
    }

    @GetMapping("/admin/{idNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Page<Claim>> getClaimsForAdmin(
            @PathVariable String idNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Claim> claimsPage = repository.findByUserIdentificationNumber(idNumber, pageable);

        return ResponseEntity.ok(claimsPage);
    }

    // Generate unique claim number
    private String generateClaimNumber() {
        String prefix = "CLM";
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(7);
        int randomNum = (int) (Math.random() * 900) + 100;
        return prefix + "-" + timestamp + "-" + randomNum;
    }
}