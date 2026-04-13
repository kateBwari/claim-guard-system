package org.kate._policyservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse <T>{
    private boolean success;   // Indicates if the operation was successful
    private String message;    // A human-readable description of the result
    private T data;       // The actual payload (Claim object, ID, or List)
}


