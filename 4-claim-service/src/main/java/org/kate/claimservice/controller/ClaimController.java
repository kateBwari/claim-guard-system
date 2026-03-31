package org.kate.claimservice.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.kate.claimservice.model.ApiResponse;
import org.kate.claimservice.model.Claim;
import org.kate.claimservice.model.ClaimDTO;
import org.kate.claimservice.service.ClaimService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    // Use constructor injection for all dependencies
    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping("/create")
    @CircuitBreaker(name = "claimServiceCircuit", fallbackMethod = "fallbackForCreateClaim")
    public ResponseEntity<ApiResponse> createClaim(@RequestBody ClaimDTO claimDTO, @AuthenticationPrincipal Jwt jwt) {
        // Business logic (mapping, checking existence, saving) moved to Service
        Claim savedClaim = claimService.createNewClaim(claimDTO, jwt.getSubject());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Claim created successfully", savedClaim));
    }

    @GetMapping("/my-claims")
    public ResponseEntity<ApiResponse> getMyClaims(Authentication authentication) {
        String idNumber = "";
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            idNumber = jwt.getClaimAsString("idNumber");
        }

        if (idNumber == null || idNumber.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Identification number missing from token", null));
        }

        List<Claim> claims = claimService.getClaimsByIdNumber(idNumber);

        if (claims.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "No claims found for the provided identification", null));
        }

        return ResponseEntity.ok(new ApiResponse(true, "Claims retrieved successfully", claims));
    }

    @DeleteMapping("/user/{userIdentificationNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse> deleteClaimsByUserId(@PathVariable String userIdentificationNumber) {
        claimService.deleteByUserIdentificationNumber(userIdentificationNumber);
        return ResponseEntity.ok(new ApiResponse(true,
                "All claims for user " + userIdentificationNumber + " have been cleared.", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse> deleteClaim(@PathVariable Long id) {
        ClaimDTO deletedClaim = claimService.deleteClaim(id);
        String message = String.format("Claim #%s belonging to User %s has been deleted.",
                deletedClaim.getClaimNumber(), deletedClaim.getUserIdentificationNumber());

        return ResponseEntity.ok(new ApiResponse(true, message, deletedClaim));
    }
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAllClaims() {
        List<ClaimDTO> allClaims = claimService.getAllClaims();

        return ResponseEntity.ok(new ApiResponse(
                true,
                "All claims retrieved successfully.",
                allClaims
        ));
    }

    // Fallback method for Circuit Breaker
    public ResponseEntity<ApiResponse> fallbackForCreateClaim(ClaimDTO claimDTO, Throwable t) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiResponse(false, "Claim Service is temporarily busy. Please try again later.", t.getMessage()));
    }
}