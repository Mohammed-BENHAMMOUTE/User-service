package inpt.aseds.userservice.domain.exceptions;

/**
 * Exception thrown when validation fails for user input.
 * This exception is safe to expose to clients.
 */
public class UserValidationException extends RuntimeException {
    
    private final String field;
    
    public UserValidationException(String message) {
        super(message);
        this.field = null;
    }
    
    public UserValidationException(String field, String message) {
        super(message);
        this.field = field;
    }
    
    public String getField() {
        return field;
    }
}
