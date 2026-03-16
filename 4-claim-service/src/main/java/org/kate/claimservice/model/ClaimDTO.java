package org.kate.claimservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClaimDTO {

    private Long id;

    private String claimNumber;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;

    private String status;

    private Long userId;

    @NotBlank(message = "Policy number is required")
    private String policyNumber;
}