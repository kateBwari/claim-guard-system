package org.kate._usersservice.controller;

import org.kate._usersservice.model.ApiResponse;
import org.kate._usersservice.model.UserDTO;
import org.kate._usersservice.UserService.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody UserDTO userDTO) {
        ApiResponse response = userService.saveUser(userDTO);
        if (!response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody UserDTO userDTO) {
        // 1. Authenticate using 'userDTO' fields directly
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword())
        );

        // 2. If valid, generate the token using the username from userDTO
        if (authenticate.isAuthenticated()) {
            String token = userService.generateToken(userDTO.getUsername());

            // 3. Wrap the token in your standardized ApiResponse
            return ResponseEntity.ok(new ApiResponse(true, "Login successful", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid credentials", null));
        }
    }
}