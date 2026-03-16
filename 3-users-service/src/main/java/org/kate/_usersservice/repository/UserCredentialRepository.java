package org.kate._usersservice.repository;

import org.kate._usersservice.model.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {
    Optional<UserCredential> findByUsername(String username);
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}
