package org.kate.claimservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kate._usersservice.model.ApiResponse;
import org.kate.claimservice.model.Policy;
import org.kate.claimservice.model.PolicyDTO;
import org.kate.claimservice.repository.PolicyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor // This generates the constructor for the final field below
public class PolicyController {

    // Fix: This must be 'final' and named 'policyRepository' to match your if-statement
    private final PolicyRepository policyRepository;

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse> addPolicy(@Valid @RequestBody PolicyDTO policyDTO) {
        // 1. Check if the policy number already exists
        if (policyRepository.existsByPolicyNumber(policyDTO.getPolicyNumber())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, "Policy number already exists!", null));
        }

        // 2. Map DTO to Entity
        Policy policy = new Policy();
        policy.setPolicyNumber(policyDTO.getPolicyNumber());
        policy.setPolicyCategory(policyDTO.getPolicyCategory());
        policy.setCoverageAmount(policyDTO.getCoverageAmount());

        // 3. Handle Date Formatting
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        policy.setExpiryDate(LocalDate.parse(policyDTO.getExpiryDate(), formatter));

        // 4. Save to Database
        Policy savedPolicy = policyRepository.save(policy);

        // 5. Map back to DTO for the standardized response
        PolicyDTO responseDTO = new PolicyDTO(
                savedPolicy.getId(),
                savedPolicy.getPolicyNumber(),
                savedPolicy.getPolicyCategory(),
                savedPolicy.getCoverageAmount(),
                savedPolicy.getExpiryDate().toString()
        );

        // 6. Return the standardized 3-argument ApiResponse
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Policy added successfully", responseDTO));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllPolicies() {
        List<Policy> policies = policyRepository.findAll();
        // Return 3 arguments: success, message, and the list as data
        return ResponseEntity.ok(new ApiResponse(true, "Policies retrieved successfully", policies));
    }
}