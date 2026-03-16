package org.kate._usersservice.UserService; // Fixed package path

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.kate._usersservice.model.ApiResponse;
import org.kate._usersservice.model.UserDTO;
import org.kate._usersservice.model.UserCredential;
import org.kate._usersservice.repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserCredentialRepository repository;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ApiResponse saveUser(@Valid UserDTO userDTO) {
        // 1. Check for duplicates BEFORE saving
        if (repository.existsByUsername(userDTO.getUsername())) {
            return new ApiResponse(false, "User is already registered!", null);
        }
        if (repository.existsByEmail(userDTO.getEmail())) {
            return new ApiResponse(false, "Email is already in use!", null);
        }

        // 2. Map DTO to Entity
        UserCredential user = new UserCredential();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        // 3. Encrypt the password (Mandatory check)
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        } else {
            return new ApiResponse(false, "Password is required", null);
        }

        // 4. Set Role (Use DTO role if present, otherwise default to ROLE_USER)
        String rawRole = (userDTO.getRole() != null) ? userDTO.getRole() : "USER";
        String formattedRole = rawRole.toUpperCase();
        if (!formattedRole.startsWith("ROLE_")) {
            formattedRole = "ROLE_" + formattedRole;
        }
        user.setRole(formattedRole);

        // 5. Save the Entity
        UserCredential savedUser = repository.save(user);

        // 6. Return the response (Note: we return savedUser, not repository.save again)
        return new ApiResponse(true, "User registered successfully!", savedUser);
    }

    // Required for AdminController to list users
    public List<UserCredential> findAllUsers() {
        return repository.findAll();
    }

    // Fixed delete method using Long to match JpaRepository
    public void deleteUserById(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        repository.deleteById(id);
    }

    // The new method for AdminController
    public UserCredential updateUserRole(Long id, String newRole) {
        // 1. Find the user first
        UserCredential user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // 2. Format the role (ensures it starts with ROLE_)
        String formattedRole = newRole.toUpperCase();
        if (!formattedRole.startsWith("ROLE_")) {
            formattedRole = "ROLE_" + formattedRole;
        }

        // 3. Save and return
        user.setRole(formattedRole);
        return repository.save(user);
    }

    public String generateToken(@NotBlank(message = "Username cannot be empty") @Size(min = 3, message = "Username must be at least 3 characters long") String username) {
        // 1. Check if the user exists in the database
        UserCredential user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        // 2. Call your JWT service to create the token
        // Assuming you have a JwtService injected as 'jwtService'
        return jwtService.generateToken(user);
    }
}

