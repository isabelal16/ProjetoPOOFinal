package controller;

import exception.LibraryException;
import model.Administrator;
import model.Librarian;
import model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Service exclusively responsible for USER AUTHENTICATION and ROLES/PERMISSIONS.
 *
 * Maintains the list of registered users (hardcoded, as permitted by the project 
 * guidelines), tracks the currently logged-in user session, and houses the 
 * requireAdmin() gatekeep method used across other services to protect restricted actions.
 */
public class AuthenticationService {

    private final List<User> users = new ArrayList<>();
    private User currentUser = null;

    public AuthenticationService() {
        // Hardcoded credentials requested by the project specifications
        users.add(new Administrator("admin", "admin123"));
        users.add(new Librarian("lib", "lib123"));
    }

    /**
     * Attempts to authenticate a user session. 
     * If credentials match, assigns the tracking reference to currentUser.
     * 
     * @return true if the login process is successful, false otherwise.
     */
    public boolean login(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.authenticate(password)) {
                currentUser = u;
                return true;
            }
        }
        return false;
    }

    /** 
     * @return the currently active logged-in User session.
     */
    public User getCurrentUser() { 
        return currentUser; 
    }

    /**
     * Halts execution and throws a LibraryException if the logged-in user is not an Administrator.
     * Centralizes authorization checks to prevent code duplication across separate application layers.
     * Uses polymorphism: currentUser.isAdmin() triggers the context-aware subclass version 
     * (Administrator -> true / Librarian -> false) automatically at runtime.
     */
    public void requireAdmin() throws LibraryException {
        if (currentUser == null || !currentUser.isAdmin()) {
            throw new LibraryException("This operation requires Administrator privileges.");
        }
    }
}