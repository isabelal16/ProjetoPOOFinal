package model;

/**
 * Represents an Administrator user with full system privileges.
 * Possesses overarching access to add, edit, and delete records.
 */
public class Administrator extends User {
    
    public Administrator(String username, String password) { 
        super(username, password); 
    }
    
    /**
     * Polymorphic implementation defining role privileges.
     * Administrators hold full system management permissions.
     */
    @Override 
    public boolean isAdmin() { 
        return true; 
    }
}