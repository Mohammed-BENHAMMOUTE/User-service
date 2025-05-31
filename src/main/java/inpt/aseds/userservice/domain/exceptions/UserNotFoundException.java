package inpt.aseds.userservice.domain.exceptions;

/**
 * Exception thrown when a user is not found in the system.
 * This exception is safe to expose to clients as it doesn't leak sensitive information.
 */
public class UserNotFoundException extends RuntimeException {
    
    private final String userIdentifier;
    
    public UserNotFoundException(String message) {
        super(message);
        this.userIdentifier = null;
    }
    
    public UserNotFoundException(Long userId) {
        super("User not found with ID: " + userId);
        this.userIdentifier = userId.toString();
    }
    
    public UserNotFoundException(String username, boolean isUsername) {
        super("User not found with username: " + username);
        this.userIdentifier = username;
    }
    
    public String getUserIdentifier() {
        return userIdentifier;
    }
}
