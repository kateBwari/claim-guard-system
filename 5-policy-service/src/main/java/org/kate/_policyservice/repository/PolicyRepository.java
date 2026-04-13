package org.kate._policyservice.repository;

import org.kate._policyservice.model.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, String> {
    boolean existsByUserIdentificationNumberAndPolicyCategory(String userId, String category);

    // ADD THIS LINE: It tells Spring to generate the SQL check for the policy number
    boolean existsByPolicyNumber(String policyNumber);

    Optional<Policy> findByPolicyNumber(String policyNumber);

    Page<Policy> findByUserIdentificationNumber(String userIdentificationNumber, Pageable pageable);

    Page<Policy> findByPolicyCategory(String policyCategory, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM Policy p WHERE p.userIdentificationNumber = :idNumber")
    void deleteByUserIdentificationNumber(String userIdentificationNumber);
}



