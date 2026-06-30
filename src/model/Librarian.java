package model;

/**
 * Represents a Librarian user handle responsible for day-to-day operations.
 * Handles tasks such as processing book checkouts (loans) and returns.
 */
public class Librarian extends User {
    
    public Librarian(String username, String password) { 
        super(username, password); 
    }
    
    /**
     * Polymorphic implementation defining role privileges.
     * Librarians do not hold administrative permissions.
     */
    @Override 
    public boolean isAdmin() { 
        return false; 
    }
}