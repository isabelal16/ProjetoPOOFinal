package model;

/**
 * Represents a library patron (customer/member).
 * 
 * PRINCIPLE - ENCAPSULATION: The borrowing history string cannot be modified 
 * directly from outside classes. It is controlled internally through addHistory(), 
 * which guarantees correct string formatting.
 */
public class Patron {
    private String id;
    private String name;
    private String contact;
    private String borrowingHistory;

    public Patron(String id, String name, String contact, String borrowingHistory) {
        this.id = id; 
        this.name = name; 
        this.contact = contact;
        this.borrowingHistory = borrowingHistory == null ? "" : borrowingHistory;
    }

    public String getId() { 
        return id; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public String getContact() { 
        return contact; 
    }
    
    public String getBorrowingHistory() { 
        return borrowingHistory; 
    }

    public void setName(String name) { 
        this.name = name; 
    }
    
    public void setContact(String contact) { 
        this.contact = contact; 
    }

    /** 
     * Appends a newly checked-out book title to the patron's history log.
     * Uses a slash separator if previous history entries exist.
     */
    public void addHistory(String bookTitle) {
        if (!borrowingHistory.isEmpty()) {
            borrowingHistory += " / ";
        }
        borrowingHistory += bookTitle;
    }

    /** 
     * Converts the object state into a CSV format string for 'patrons.txt'.
     * Format: id;name;contact;history
     */
    @Override
    public String toString() {
        return id + ";" + name + ";" + contact + ";" + borrowingHistory;
    }

    /** 
     * Performs a case-insensitive search matching the query against
     * the patron's name or ID fields.
     */
    public boolean matchesSearch(String query) {
        String q = query.toLowerCase();
        return name.toLowerCase().contains(q) || id.toLowerCase().contains(q);
    }
}