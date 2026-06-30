package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents an active transactional loan, linking a Book (via ISBN) 
 * to a library Patron (via ID).
 * Enforces business logic rules regarding FINES: $2.00 rate per overdue day.
 */
public class Loan {
    private String bookIsbn;
    private String patronId;
    private LocalDate loanDate;
    private LocalDate dueDate;

    public Loan(String bookIsbn, String patronId, LocalDate loanDate, LocalDate dueDate) {
        this.bookIsbn = bookIsbn; 
        this.patronId = patronId;
        this.loanDate = loanDate; 
        this.dueDate = dueDate;
    }

    public String getBookIsbn() { 
        return bookIsbn; 
    }
    
    public String getPatronId() { 
        return patronId; 
    }
    
    public LocalDate getLoanDate() { 
        return loanDate; 
    }
    
    public LocalDate getDueDate() { 
        return dueDate; 
    }

    /** 
     * Business Logic Rule: Calculates a fine of $2.00 per late day.
     * Returns 0.0 if the book is still within the active loan period.
     */
    public double calculateFine() {
        long days = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        return days > 0 ? days * 2.0 : 0.0;
    }

    /** 
     * Checks if the current calendar date has passed the agreed due date.
     */
    public boolean isOverdue() { 
        return LocalDate.now().isAfter(dueDate); 
    }

    /** 
     * Converts the object state into a CSV format string for 'loans.txt'.
     * Format: isbn;patronId;loanDate;dueDate
     */
    @Override
    public String toString() {
        return bookIsbn + ";" + patronId + ";" + loanDate + ";" + dueDate;
    }
}