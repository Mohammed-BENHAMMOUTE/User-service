package inpt.aseds.userservice.application.services;

import inpt.aseds.userservice.domain.exceptions.UserNotFoundException;
import inpt.aseds.userservice.domain.exceptions.UserOperationException;
import inpt.aseds.userservice.domain.model.User;
import inpt.aseds.userservice.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service 
@Transactional
public class UserService {
    
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    private final ValidationService validationService;
    
    public UserService(UserRepository userRepository, ValidationService validationService) {
        this.userRepository = userRepository;
        this.validationService = validationService;
    }
    
    /**
     * Retrieves a user by ID.
     */
    public User getUserById(Long id) {
        validationService.validateUserId(id);
        
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
    
    /**
     * Retrieves a user by username.
     */
    public User getUserByUsername(String username) {
        validationService.validateUsername(username);
        
        return userRepository.findUserByUsername(username.trim())
                .orElseThrow(() -> new UserNotFoundException(username, true));
    }
    
    /**
     * Updates an existing user.
     */
    public User updateUser(Long id, User updates) {
        validationService.validateUserId(id);
        
        User existingUser = getUserById(id);
        
        // Validate and apply updates
        if (StringUtils.hasText(updates.getUsername())) {
            validationService.validateUsername(updates.getUsername());
            
            // Check if username is already taken by another user
            Optional<User> userWithSameUsername = userRepository.findUserByUsername(updates.getUsername().trim());
            if (userWithSameUsername.isPresent() && !userWithSameUsername.get().getId().equals(id)) {
                throw new UserOperationException("update", "Username is already taken");
            }
            
            existingUser.setUsername(updates.getUsername().trim());
        }
        
        if (StringUtils.hasText(updates.getEmail())) {
            validationService.validateEmail(updates.getEmail());
            
            // Check if email is already taken by another user
            Optional<User> userWithSameEmail = userRepository.findByEmail(updates.getEmail().trim());
            if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(id)) {
                throw new UserOperationException("update", "Email is already taken");
            }
            
            existingUser.setEmail(updates.getEmail().trim());
        }
        
        if (StringUtils.hasText(updates.getBio())) {
            validationService.validateBio(updates.getBio());
            existingUser.setBio(updates.getBio().trim());
        }
        
        if (StringUtils.hasText(updates.getAvatarUrl())) {
            existingUser.setAvatarUrl(updates.getAvatarUrl().trim());
        }
        
        if (StringUtils.hasText(updates.getChannelId())) {
            existingUser.setChannelId(updates.getChannelId().trim());
        }
        
        try {
            User savedUser = userRepository.save(existingUser);
            log.info("User updated successfully: {}", savedUser.getId());
            return savedUser;
        } catch (Exception e) {
            log.error("Failed to update user with ID: {}", id, e);
            throw new UserOperationException("update", "Failed to update user");
        }
    }
    
    /**
     * Deactivates a user (soft delete).
     */
    public void deactivateUser(Long id) {
        validationService.validateUserId(id);
        
        User user = getUserById(id);
        
        if (!user.isActive()) {
            throw new UserOperationException("deactivate", "User is already deactivated");
        }
        
        user.setActive(false);
        
        try {
            userRepository.save(user);
            log.info("User deactivated successfully: {}", id);
        } catch (Exception e) {
            log.error("Failed to deactivate user with ID: {}", id, e);
            throw new UserOperationException("deactivate", "Failed to deactivate user");
        }
    }
    
    /**
     * Hard deletes a user from the system.
     */
    public void deleteUser(Long id) {
        validationService.validateUserId(id);
        
        User user = getUserById(id);
        
        try {
            userRepository.delete(user);
            log.info("User deleted successfully: {}", id);
        } catch (Exception e) {
            log.error("Failed to delete user with ID: {}", id, e);
            throw new UserOperationException("delete", "Failed to delete user");
        }
    }
    
    /**
     * Lists all users with pagination.
     */
    public Page<User> getAllUsers(Pageable pageable) {
        validationService.validatePagination(pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            return userRepository.findAllActiveUsers(pageable);
        } catch (Exception e) {
            log.error("Failed to retrieve users", e);
            throw new UserOperationException("list", "Failed to retrieve users");
        }
    }
    
    /**
     * Searches users by username with pagination.
     */
    public Page<User> searchUsers(String query, Pageable pageable) {
        validationService.validateSearchQuery(query);
        validationService.validatePagination(pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            return userRepository.searchUsersByUsername(query.trim(), pageable);
        } catch (Exception e) {
            log.error("Failed to search users with query: {}", query, e);
            throw new UserOperationException("search", "Failed to search users");
        }
    }
    
    /**
     * Checks if a username exists in the system.
     */
    public boolean usernameExists(String username) {
        validationService.validateUsername(username);
        
        try {
            return userRepository.existsByUsername(username.trim());
        } catch (Exception e) {
            log.error("Failed to check username existence: {}", username, e);
            throw new UserOperationException("check", "Failed to check username availability");
        }
    }
    
    /**
     * Checks if an email exists in the system.
     */
    public boolean emailExists(String email) {
        validationService.validateEmail(email);
        
        try {
            return userRepository.existsByEmail(email.trim());
        } catch (Exception e) {
            log.error("Failed to check email existence: {}", email, e);
            throw new UserOperationException("check", "Failed to check email availability");
        }
    }
}
