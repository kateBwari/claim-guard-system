package org.kate._usersservice.controller;

import org.kate._usersservice.model.UserCredential;
import org.kate._usersservice.UserService.UserService;
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

    // Delete a user by ID
    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        service.deleteUserById(id);
        return ResponseEntity.ok("User deleted successfully by admin.");
    }

    // Update user roles or status
    @PutMapping("/update-role/{id}")
    public ResponseEntity<UserCredential> updateRole(@PathVariable Long id, @RequestBody String newRole) {
        return ResponseEntity.ok(service.updateUserRole(id, newRole));
    }
}
