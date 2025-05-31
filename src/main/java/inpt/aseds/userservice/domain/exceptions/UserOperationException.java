package inpt.aseds.userservice.domain.exceptions;

/**
 * Exception thrown when a user operation is not allowed.
 * This exception is safe to expose to clients.
 */
public class UserOperationException extends RuntimeException {
    
    private final String operation;
    
    public UserOperationException(String message) {
        super(message);
        this.operation = null;
    }
    
    public UserOperationException(String operation, String message) {
        super(message);
        this.operation = operation;
    }
    
    public String getOperation() {
        return operation;
    }
}
