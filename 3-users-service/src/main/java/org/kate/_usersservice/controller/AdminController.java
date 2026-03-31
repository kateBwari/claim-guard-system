package org.kate._usersservice.controller;

import org.kate._usersservice.model.ApiResponse;
import org.kate._usersservice.model.UserCredential;
import org.kate._usersservice.UserService.UserService;
import org.kate._usersservice.model.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin") // The Gateway should route /api/admin/** here
public class AdminController {

    @Autowired
    private UserService service;

    // Get all users - Admin only functionality
    @GetMapping("/all-users")
    public ResponseEntity<List<UserCredential>> getAllUsers() {
        return ResponseEntity.ok(service.findAllUsers());
    }

    @DeleteMapping("/delete-user/{userIdentificationNumber}")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable String userIdentificationNumber) {
        // 1. (Optional) Get user info first if you want it in the response
        // var user = service.getUserById(id);

        UserDTO deletedUser = service.deleteByuserIdentificationNumber(userIdentificationNumber);

        // 2. Create the response without the gray labels
        ApiResponse<Object> response = new ApiResponse<>(
                true,
                "User with ID " + userIdentificationNumber + " deleted successfully by admin",
                deletedUser // Or pass the 'user' object here if you fetched it
        );

        return ResponseEntity.ok(response);

    }
}