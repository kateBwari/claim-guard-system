package org.kate.claimservice.controller;

import lombok.RequiredArgsConstructor;
import org.kate._usersservice.model.ApiResponse;
import org.kate.claimservice.model.Claim;
import org.kate.claimservice.model.ClaimDTO;
import org.kate.claimservice.repository.ClaimRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor // Automatically handles injection of the repository
public class ClaimController {

    private final ClaimRepository repository;

    /**
     * CREATE CLAIM
     * Standardized to return: success, message, and the new Claim ID.
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createClaim(
            @RequestBody ClaimDTO claimDTO,
            @RequestHeader("X-Auth-User-Id") Long userId
    ) {
        // 1. Map DTO to Entity
        Claim claim = new Claim();
        claim.setClaimNumber(generateClaimNumber());
        claim.setDescription(claimDTO.getDescription());
        claim.setAmount(claimDTO.getAmount());
        claim.setStatus("PENDING"); // Default status for new claims
        claim.setUserId(userId);    // From the authenticated header
        claim.setPolicyNumber(claimDTO.getPolicyNumber());

        // 2. Save the claim
        Claim saved = repository.save(claim);

        // 3. Return 3 arguments: success, message, data (ID)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Claim created successfully", saved.getId()));
    }

    /**
     * DELETE CLAIM
     * Restricted to ADMIN/MANAGER roles.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse> deleteClaim(@PathVariable Long id) {
        // 1. Check existence to provide a better error message
        if (repository.existsById(id)) {
            repository.deleteById(id);

            // 2. Return 3 arguments: success, message, data (null for delete)
            return ResponseEntity.ok(new ApiResponse(true, "Claim deleted successfully", null));
        }

        // 3. Error case follows the same structure
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, "Claim not found with id: " + id, null));
    }

    /**
     * GET USER CLAIMS
     * Fetches claims specific to the logged-in user.
     */
    @GetMapping("/my-claims")
    public ResponseEntity<ApiResponse> getMyClaims(@RequestHeader("X-Auth-User-Id") Long userId) {
        // 1. Fetch from repository
        List<Claim> claims = repository.findByUserId(userId);

        // 2. Map to DTO list for a clean "data" argument
        List<ClaimDTO> responseData = claims.stream()
                .map(claim -> new ClaimDTO(
                        claim.getId(),
                        claim.getClaimNumber(),
                        claim.getDescription(),
                        claim.getAmount(),
                        claim.getStatus(),
                        claim.getUserId(),
                        claim.getPolicyNumber()
                ))
                .toList();

        // 3. Return 3 arguments: success, message, data (The List)
        return ResponseEntity.ok(new ApiResponse(true, "Claims retrieved successfully", responseData));
    }
    // Add this at the bottom of the class
    private String generateClaimNumber() {
        String prefix = "CLM";
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(7);
        int randomNum = (int) (Math.random() * 900) + 100;
        return prefix + "-" + timestamp + "-" + randomNum;
    }
}