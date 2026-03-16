package org.kate.claimservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimResponse {
    // A friendly message like "Claim created successfully"
    private String message;

    // To help the frontend/phone app quickly check if the operation worked
    private boolean success;

    // Optional: Return the ID of the affected claim
    private Long claimId;
}
