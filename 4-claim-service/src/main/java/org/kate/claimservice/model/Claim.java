package org.kate.claimservice.model;

import jakarta.persistence.*;
import lombok.*; // Using specific annotations is safer for JPA

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "claims")
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String claimNumber;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double amount;

    private String status;

    @Column(nullable = false)
    private String policyNumber;

    @Column(name = "user_id")
    private Long userId;
}