package org.kate._apigatewayservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClaimDto {

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