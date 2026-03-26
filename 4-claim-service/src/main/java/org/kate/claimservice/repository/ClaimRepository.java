package org.kate.claimservice.repository;

import org.kate.claimservice.model.Claim; // This must match your model's package
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    boolean existsByPolicyNumberAndDescription(String policyNumber, String description);

    Page<Claim> findByUserIdentificationNumber(String userIdentificationNumber, Pageable pageable);

    List<Claim> findByUserIdentificationNumber(String userIdentificationNumber);
    @Transactional
    void deleteByUserIdentificationNumber(String userIdentificationNumber);
}