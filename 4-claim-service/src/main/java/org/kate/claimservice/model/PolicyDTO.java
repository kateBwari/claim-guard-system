package org.kate.claimservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyDTO {

    private Long id;

    @NotBlank(message = "Policy number cannot be blank")
    @Size(min = 5, message = "Policy number must be at least 5 characters")
    private String policyNumber;

    @NotBlank(message = "Please specify the policy type")
    private String policyCategory;

    @Positive(message = "Coverage amount must be a positive value")
    private Double coverageAmount;

    @NotBlank(message = "Expiry date is required")
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Use format DD/MM/YYYY")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private String expiryDate;
}