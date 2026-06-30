package exception;

/**
 * Custom checked exception used for library business rule violations.
 * * Since it extends Exception (making it a checked exception), the compiler 
 * forces the caller (the View layer) to handle it via try-catch blocks.
 * This guarantees errors become user-friendly messages instead of crashes.
 */
public class LibraryException extends Exception {
    
    /**
     * Constructs a new exception holding the specific error description message.
     */
    public LibraryException(String message) { 
        super(message); 
    }
}