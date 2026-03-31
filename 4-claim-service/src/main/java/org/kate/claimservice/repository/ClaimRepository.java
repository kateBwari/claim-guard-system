package org.kate.claimservice.repository;

import org.kate.claimservice.model.Claim; // This must match your model's package
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    boolean existsByPolicyNumberAndDescriptionAndUserIdentificationNumber(String policyNumber, String description, String userIdentificationNumber);

    Page<Claim> findByUserIdentificationNumber(String userIdentificationNumber, Pageable pageable);

    List<Claim> findByUserIdentificationNumber(String userIdentificationNumber);

    Page<Claim> findByUserIdentificationNumberAndBenefitType(String userIdentificationNumber, String benefitType, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM Claim c WHERE c.userIdentificationNumber = :idNumber")
    void deleteByUserIdentificationNumber(@Param("idNumber")String userIdentificationNumber);

    Page<Claim> findByBenefitType(String benefitType, Pageable pageable);
}