package org.kate._policyservice.dto;

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

    private String policyNumber;

    @NotBlank(message = "Identification number is required")
    @Size(min = 8, message = "Identification number must be at least 8 digits")
    @Pattern(regexp = "^[0-9]+$", message = "Identification number must contain only numbers")
    private String userIdentificationNumber;

    @NotBlank(message = "Please specify the policy type")
    private String policyCategory;

    @Positive(message = "Premium amount must be a positive value")
    private Double premium;

    @NotBlank(message = "Expiry date is required")
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Use format DD/MM/YYYY")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private String expiryDate;
}