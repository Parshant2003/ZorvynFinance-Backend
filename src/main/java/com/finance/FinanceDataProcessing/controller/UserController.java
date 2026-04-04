package com.finance.FinanceDataProcessing.controller;


import com.finance.FinanceDataProcessing.dtos.UserResponse;
import com.finance.FinanceDataProcessing.model.Role;
import com.finance.FinanceDataProcessing.model.User;
import com.finance.FinanceDataProcessing.security.JwtUtil;
import com.finance.FinanceDataProcessing.service.AuthService;
import com.finance.FinanceDataProcessing.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User management endpoints (Admin only)")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Get all users (ADMIN only)
     * GET /api/users
     */
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve all users (Admin only)")
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable, HttpServletRequest request) {
        log.info("Fetching all users");
        User currentUser = getCurrentUser(request);
        Page<UserResponse> users = userService.getAllUsers(pageable, currentUser);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Get user by ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve user details by ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id, HttpServletRequest request) {
        log.info("Fetching user: {}", id);
        User currentUser = getCurrentUser(request);
        UserResponse user = userService.getUserById(id, currentUser);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Update user role and status (ADMIN only)
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user role and status (Admin only)")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean active,
            HttpServletRequest request) {
        log.info("Updating user: {}", id);
        User currentUser = getCurrentUser(request);
        UserResponse updatedUser = userService.updateUser(id, role, active, currentUser);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    /**
     * Delete/Deactivate user (ADMIN only)
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deactivate user account (Admin only)")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        log.info("Deleting user: {}", id);
        User currentUser = getCurrentUser(request);
        userService.deleteUser(id, currentUser);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User deactivated successfully");
        response.put("userId", id.toString());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Helper method to extract current user from JWT token
     */
    private User getCurrentUser(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        String email = jwtUtil.extractEmail(token);
        return authService.getUserByEmail(email);
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new RuntimeException("Invalid or missing Authorization header");
    }
}