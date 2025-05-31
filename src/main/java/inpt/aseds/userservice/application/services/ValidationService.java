package inpt.aseds.userservice.application.services;

import inpt.aseds.userservice.application.config.UserServiceProperties;
import inpt.aseds.userservice.domain.exceptions.UserValidationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Service for validating user inputs and operations.
 * Uses configuration properties from application.yaml for validation rules.
 */
@Service
public class ValidationService {
    
    private final UserServiceProperties properties;
    
    public ValidationService(UserServiceProperties properties) {
        this.properties = properties;
    }
    
    /**
     * Validates pagination parameters.
     */
    public void validatePagination(int page, int size) {
        if (page < 0) {
            throw new UserValidationException("page", "Page number cannot be negative");
        }
        if (size <= 0) {
            throw new UserValidationException("size", "Page size must be positive");
        }
        if (size > properties.getPagination().getMaxPageSize()) {
            throw new UserValidationException("size", 
                "Page size cannot exceed " + properties.getPagination().getMaxPageSize());
        }
    }
    
    /**
     * Validates search query.
     */
    public void validateSearchQuery(String query) {
        if (!StringUtils.hasText(query)) {
            throw new UserValidationException("query", "Search query cannot be empty");
        }
        if (query.trim().length() < properties.getValidation().getSearch().getMinQueryLength()) {
            throw new UserValidationException("query",
                "Search query must be at least " + properties.getValidation().getSearch().getMinQueryLength() + " characters long");
        }
    }
    
    /**
     * Validates username format and length.
     */
    public void validateUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new UserValidationException("username", "Username cannot be empty");
        }
        
        String trimmedUsername = username.trim();
        if (trimmedUsername.length() < properties.getValidation().getUsername().getMinLength()) {
            throw new UserValidationException("username", 
                "Username must be at least " + properties.getValidation().getUsername().getMinLength() + " characters");
        }
        if (trimmedUsername.length() > properties.getValidation().getUsername().getMaxLength()) {
            throw new UserValidationException("username", 
                "Username cannot exceed " + properties.getValidation().getUsername().getMaxLength() + " characters");
        }
        
        // Basic username validation - alphanumeric and underscores only
        if (!trimmedUsername.matches("^[a-zA-Z0-9_]+$")) {
            throw new UserValidationException("username", 
                "Username can only contain letters, numbers, and underscores");
        }
    }
    
    /**
     * Validates email format and length.
     */
    public void validateEmail(String email) {
        if (StringUtils.hasText(email)) {
            if (email.length() > properties.getValidation().getEmail().getMaxLength()) {
                throw new UserValidationException("email", 
                    "Email cannot exceed " + properties.getValidation().getEmail().getMaxLength() + " characters");
            }
            
            String emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
            if (!email.matches(emailRegex)) {
                throw new UserValidationException("email", "Invalid email format");
            }
        }
    }
    
    /**
     * Validates bio length.
     */
    public void validateBio(String bio) {
        if (StringUtils.hasText(bio) && bio.length() > properties.getValidation().getBio().getMaxLength()) {
            throw new UserValidationException("bio", 
                "Bio cannot exceed " + properties.getValidation().getBio().getMaxLength() + " characters");
        }
    }
    
    /**
     * Validates user ID.
     */
    public void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new UserValidationException("id", "User ID must be a positive number");
        }
    }
}
