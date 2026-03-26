package org.kate.claimservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Use Lombok to save time
@AllArgsConstructor
@NoArgsConstructor
public class ClaimCreatedEvent {
    private Long Id;
    private String userIdentificationNumber;
    private String policyNumber;
}
