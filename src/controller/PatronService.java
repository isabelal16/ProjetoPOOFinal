package controller;

import exception.LibraryException;
import model.Loan;
import model.Patron;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles all core operations related to PATRONS: creation, updates, deletions, and history logs.
 * Manages its own internal collection tracking registered library members.
 */
public class PatronService {

    private final List<Patron> patrons = new ArrayList<>();
    private final AuthenticationService auth;
    private final DataManager dataManager;

    public PatronService(AuthenticationService auth, DataManager dataManager) {
        this.auth = auth;
        this.dataManager = dataManager;
        dataManager.loadPatrons(patrons); // Loads existing records upon system initialization
    }

    /** 
     * Retrieves the internal database listing of registered users.
     */
    public List<Patron> getPatrons() { 
        return patrons; 
    }

    /** 
     * Registers a new library member. Restricted to Admins; Patron ID must be unique.
     */
    public void addPatron(Patron patron) throws LibraryException {
        auth.requireAdmin();
        for (Patron p : patrons) {
            if (p.getId().equalsIgnoreCase(patron.getId())) {
                throw new LibraryException("Patron ID already registered: " + patron.getId());
            }
        }
        patrons.add(patron);
        dataManager.savePatrons(patrons);
    }

    /** 
     * Updates profile contact details for an existing member record.
     */
    public void editPatron(String id, String newName, String newContact) throws LibraryException {
        auth.requireAdmin();
        Patron p = findPatron(id);
        p.setName(newName);
        p.setContact(newContact);
        dataManager.savePatrons(patrons);
    }

    /**
     * Removes a user profile record from the registry database tracking index.
     * Restricted to Admins. Blocks deletion if there are outstanding/unreturned items.
     */
    public void deletePatron(String id, List<Loan> loans) throws LibraryException {
        auth.requireAdmin();
        for (Loan l : loans) {
            if (l.getPatronId().equalsIgnoreCase(id)) {
                throw new LibraryException("Cannot delete a patron profile with active loans pending return.");
            }
        }
        if (!patrons.removeIf(p -> p.getId().equalsIgnoreCase(id))) {
            throw new LibraryException("Patron record not found for ID: " + id);
        }
        dataManager.savePatrons(patrons);
    }

    /** 
     * Filters members with case-insensitive search matching against IDs or names.
     */
    public List<Patron> searchPatrons(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(patrons);
        }
        return patrons.stream()
                .filter(p -> p.matchesSearch(query))
                .collect(Collectors.toList());
    }

    /** 
     * Returns historical checkout list text statements, fallback safe text if unrecorded.
     */
    public String getPatronHistory(String patronId) throws LibraryException {
        Patron p = findPatron(patronId);
        String h = p.getBorrowingHistory();
        return h.trim().isEmpty() ? "(no borrowing history)" : h;
    }

    /** 
     * Identifies individual instances via structured unique primary token fields.
     */
    public Patron findPatron(String id) throws LibraryException {
        return patrons.stream()
                .filter(p -> p.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElseThrow(() -> new LibraryException("Patron record not found for ID: " + id));
    }
}