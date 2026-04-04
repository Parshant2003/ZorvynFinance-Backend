package com.finance.FinanceDataProcessing.service;

import com.finance.FinanceDataProcessing.dtos.UserResponse;
import com.finance.FinanceDataProcessing.exception.AccessDeniedException;
import com.finance.FinanceDataProcessing.exception.ResourceNotFoundException;
import com.finance.FinanceDataProcessing.model.Role;
import com.finance.FinanceDataProcessing.model.User;
import com.finance.FinanceDataProcessing.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all users (ADMIN only)
     */
    public Page<UserResponse> getAllUsers(Pageable pageable, User currentUser) {
        if (!currentUser.getRole().canManageUsers()) {
            throw new AccessDeniedException("Only admins can view all users");
        }

        log.info("Fetching all users");
        return userRepository.findAll(pageable)
                .map(this::mapToUserResponse);
    }

    /**
     * Get user by ID
     */
    public UserResponse getUserById(Long id, User currentUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Users can only view their own profile unless admin
        if (!currentUser.getRole().canManageUsers() && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You can only view your own profile");
        }

        log.info("Fetching user: {}", id);
        return mapToUserResponse(user);
    }

    /**
     * Update user role and status (ADMIN only)
     */
    public UserResponse updateUser(Long id, Role role, Boolean active, User currentUser) {
        if (!currentUser.getRole().canManageUsers()) {
            throw new AccessDeniedException("Only admins can update users");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        if (role != null) {
            user.setRole(role);
        }
        if (active != null) {
            user.setActive(active);
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated: {}", id);

        return mapToUserResponse(updatedUser);
    }

    /**
     * Delete/Deactivate user (ADMIN only)
     */
    public void deleteUser(Long id, User currentUser) {
        if (!currentUser.getRole().canManageUsers()) {
            throw new AccessDeniedException("Only admins can delete users");
        }

        if (currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You cannot delete your own account");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        user.setActive(false);
        userRepository.save(user);
        log.info("User deactivated: {}", id);
    }

    /**
     * Map User entity to UserResponse DTO
     */
    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}