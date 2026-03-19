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
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    // 1. REGISTRATION: Create a new user
    public ApiResponse<UserCredential> saveUser(@Valid UserDTO userDTO) {
        if (repository.existsByUsername(userDTO.getUsername())) {
            return new ApiResponse<>(false, "Username is already taken!", null);
        }
        if (repository.existsByEmail(userDTO.getEmail())) {
            return new ApiResponse<>(false, "Email is already in use!", null);
        }

        UserCredential user = new UserCredential();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // Default to ROLE_USER if not specified
        String role = (userDTO.getRole() != null) ? userDTO.getRole().toUpperCase() : "ROLE_USER";
        if (!role.startsWith("ROLE_")) role = "ROLE_" + role;
        user.setRole(role);

        UserCredential savedUser = repository.save(user);
        return new ApiResponse<>(true, "User registered successfully!", savedUser);
    }

    // 2. ADMIN: Update a user's role
    public UserDTO updateUserRole(Long id, UserDTO userDetails) {
        UserCredential user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (userDetails.getRole() != null) {
            String role = userDetails.getRole().toUpperCase();
            if (!role.startsWith("ROLE_")) role = "ROLE_" + role;
            user.setRole(role);
        }

        UserCredential updated = repository.save(user);
        return mapToDTO(updated);
    }

    // 3. USER: Update own profile (Email, etc.)
    public UserDTO updateUserProfile(String currentUsername, UserDTO profileUpdate) {
        UserCredential user = repository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found: " + currentUsername));

        if (profileUpdate.getEmail() != null) {
            user.setEmail(profileUpdate.getEmail());
        }

        UserCredential saved = repository.save(user);
        return mapToDTO(saved);
    }
    // Helper: Find all users for the Admin panel
    public List<UserCredential> findAllUsers() {
        return repository.findAll();
    }
    private UserDTO mapToDTO(UserCredential user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        // Password is never mapped to DTO for security
        return dto;
    }
    public String getUsernameById(Long id) {
            // 1. Attempt to find the user in the repository
            return repository.findById(id)
                    .map(UserCredential::getUsername) // Extract just the username if found
                    .orElse("User Not Found");        // Return a clear message if not found
    }
    public String generateToken(@NotBlank String username) {
        // 1. You named this variable 'user'
        UserCredential user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        // 2. So you must use 'user.getId()', not 'savedUser.getId()'
        return jwtService.generateToken(user);
    }

    public UserDTO deleteUserById(Long id) {
            // 1. Fetch the user first (crucial for getting the username)
            UserCredential user = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

            // 2. Map to DTO before deleting from the database
            UserDTO deletedInfo = mapToDTO(user);

            // 3. Delete the user
            repository.delete(user);

            // 4. Return the data to the controller
            return deletedInfo;
        }}


