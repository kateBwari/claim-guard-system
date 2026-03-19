package org.kate.claimservice.repository;

import org.kate.claimservice.model.Claim; // This must match your model's package
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    boolean existsByPolicyNumberAndDescription(String policyNumber, String description);
    List<Claim> findByUserId(Long userId);
}