package org.kate.claimservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Policy number is required")
    @Size(min = 5, message = "Policy number must be at least 5 characters")
    @Column(unique = true)
    private String policyNumber;

    @NotBlank(message = "Policy type is required (e.g., LIFE, HEALTH)")
    private String policyCategory;

    @Positive(message = "Coverage amount must be greater than zero")
    private double coverageAmount;

    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;
}