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

    @NotBlank(message = "Identification number is required")
    private String userIdentificationNumber;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotBlank(message = "Benefit type is required")
    private String benefitType;


    private String status;

    @NotBlank(message = "Policy number is required")
    private String policyNumber;
}