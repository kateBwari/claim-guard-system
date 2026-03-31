package org.kate.claimservice.repository;

import org.kate.claimservice.model.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    boolean existsByUserIdentificationNumberAndPolicyCategory(String userId, String category);

    // ADD THIS LINE: It tells Spring to generate the SQL check for the policy number
    boolean existsByPolicyNumber(String policyNumber);

    Optional<Policy> findByPolicyNumber(String policyNumber);

    Page<Policy> findByUserIdentificationNumber(String userIdentificationNumber, Pageable pageable);

    Page<Policy> findByPolicyCategory(String policyCategory, Pageable pageable);
}