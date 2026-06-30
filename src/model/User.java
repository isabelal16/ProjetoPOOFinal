package model;

/**
 * Abstract superclass representing a system user capable of logging in.
 * 
 * PRINCIPLE - ENCAPSULATION: Attributes use the 'protected' access modifier,
 * meaning they can only be directly accessed by extending subclasses.
 * 
 * PRINCIPLE - INHERITANCE: Concrete classes like Administrator and Librarian
 * inherit the username, password fields, and the authenticate() method.
 * 
 * PRINCIPLE - POLYMORPHISM: The isAdmin() method is declared abstract, forcing
 * each specific subclass to define its own access level return logic.
 */
public abstract class User {
    protected String username;
    protected String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { 
        return username; 
    }

    /** 
     * Validates credentials by checking if the provided password matches.
     */
    public boolean authenticate(String pass) {
        return this.password.equals(pass);
    }

    /** 
     * Polymorphic method hook.
     * Implementation in Administrator returns true, while Librarian returns false.
     */
    public abstract boolean isAdmin();
}