package org.kate.claimservice.model;

import jakarta.persistence.*;
import lombok.*; // Using specific annotations is safer for JPA
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

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

    @Column(nullable = false, unique =true)
    private String userIdentificationNumber;


    @Column(columnDefinition = "TEXT")
    private String description;


    @Column(nullable = false)
    private String benefitType; // e.g., "Death Benefit", "Accident Coverage"

    private String status;

    @Column(nullable = false)
    private String policyNumber;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    @PrePersist
    public void prePersist(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate(){
        updatedAt = LocalDateTime.now();
    }
}
