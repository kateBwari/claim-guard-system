package org.kate.frauddetectionservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimRequest {
    // Fields matching your ClaimDTO (image_4cd104.png)
    private Long id;
    private String claimNumber;
    private String userIdentificationNumber;
    private String description;
    private String benefitType;
    private String status;
    private String policyNumber;

    // Fields required for the Fraud Logic we discussed
    private Double amount;
    private Integer policyAgeInDays;
    private String fraudStatus;        // This will be "FLAGGED" or "APPROVED"
    private String reason;             // The explanation from Drools
}
