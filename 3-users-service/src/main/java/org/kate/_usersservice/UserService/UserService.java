package org.kate._usersservice.UserService; // Fixed package path

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.kate._usersservice.exception.ResourceNotFoundException;
import org.kate._usersservice.model.ApiResponse;
import org.kate._usersservice.model.UserDTO;
import org.kate._usersservice.model.UserCredential;
import org.kate._usersservice.repository.UserCredentialRepository;
import org.springframework.amqp.rabbit.core.RabbitMessageOperations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserCredentialRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ClaimClient claimClient;
    @Autowired
    private RabbitTemplate rabbitTemplate;


    // 1. REGISTRATION: Create a new user
    public ApiResponse<UserCredential> saveUser(@Valid UserDTO userDTO) {
        if (repository.existsByUsername(userDTO.getUsername())) {
            return new ApiResponse<>(false, "Username is already taken!", null);
        }
        if (repository.existsByEmail(userDTO.getEmail())) {
            return new ApiResponse<>(false, "Email is already in use!", null);
        }
        if (repository.existsByUserIdentificationNumber(userDTO.getUserIdentificationNumber())) {
            return new ApiResponse(false, "This identification number is already in use", null);
        }
        UserCredential user = new UserCredential();
        user.setUsername(userDTO.getUsername());
        user.setUserIdentificationNumber(userDTO.getUserIdentificationNumber());
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
        dto.setUserIdentificationNumber(user.getUserIdentificationNumber());
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
    @Transactional
    public UserDTO deleteByuserIdentificationNumber(String userIdentificationNumber) {
        // 1. Find the user first to make sure they exist
        UserCredential user = repository.findByuserIdentificationNumber(userIdentificationNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userIdentificationNumber));

        // 2. Map to DTO before deleting so you can return the info
        UserDTO userDto = new UserDTO();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setUserIdentificationNumber(user.getUserIdentificationNumber());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());
// Set any other fields you need here

        // 3. Delete from database
        repository.deleteByuserIdentificationNumber((userIdentificationNumber));
        rabbitTemplate.convertAndSend("user.deletion.exchange", "", userIdentificationNumber);
        System.out.println("Message sent to Rabbitmq");
        return userDto;
    }
}