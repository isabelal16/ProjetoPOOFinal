package controller;

import exception.LibraryException;
import model.Book;
import model.Loan;
import model.Patron;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles transactional workflow operations: Book checkouts, returns, and track delays.
 * This stands as the primary structural anchor containing core system business rules.
 * 
 * Reuses instance references from BookService and PatronService to locate resources,
 * ensuring logical continuity without duplication.
 */
public class LoanService {

    /** Strict legal lending return threshold timeframe configuration. */
    private static final int LOAN_PERIOD_DAYS = 14;

    private final List<Loan> loans = new ArrayList<>();
    private final BookService bookService;
    private final PatronService patronService;
    private final DataManager dataManager;

    public LoanService(BookService bookService, PatronService patronService, DataManager dataManager) {
        this.bookService = bookService;
        this.patronService = patronService;
        this.dataManager = dataManager;
        dataManager.loadLoans(loans); // Loads active logs upon system initialization
    }

    /** 
     * Retrieves the master list tracking active checked-out items.
     */
    public List<Loan> getLoans() { 
        return loans; 
    }

    /**
     * Executes transaction checkout binding a Book instance to a library Patron.
     * 
     * Business rules validation:
     *  - Target references must exist;
     *  - Available item inventory balance must be greater than zero;
     *  - Patrons cannot check out duplicate copies of the same book concurrently.
     * 
     * Process: Decrements available item metrics, appends log text to member history, 
     * and maps a new 14-day duration expiration date stamp tracker.
     */
    public void checkoutBook(String isbn, String patronId) throws LibraryException {
        Book book = bookService.findBook(isbn);
        Patron patron = patronService.findPatron(patronId);

        if (book.getAvailableCopies() <= 0) {
            throw new LibraryException("No available copies remaining for this book.");
        }

        for (Loan l : loans) {
            if (l.getBookIsbn().equalsIgnoreCase(isbn) && l.getPatronId().equalsIgnoreCase(patronId)) {
                throw new LibraryException("This patron currently holds an active unreturned copy of this book.");
            }
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        patron.addHistory(book.getTitle());

        LocalDate today = LocalDate.now();
        loans.add(new Loan(isbn, patronId, today, today.plusDays(LOAN_PERIOD_DAYS)));
        saveAll();
    }

    /**
     * Processes inventory returns. 
     * Computes accrued overdue fine obligations BEFORE clearing transaction lines, 
     * restores inventory balance metrics, and outputs result status receipts.
     */
    public String returnBook(String isbn, String patronId) throws LibraryException {
        Loan loan = loans.stream()
                .filter(l -> l.getBookIsbn().equalsIgnoreCase(isbn) && l.getPatronId().equalsIgnoreCase(patronId))
                .findFirst()
                .orElseThrow(() -> new LibraryException("Active loan matching criteria not found."));

        double fine = loan.calculateFine();
        loans.remove(loan);

        // Increments available copy count metrics back to the main catalog
        bookService.getBooks().stream()
                .filter(b -> b.getIsbn().equalsIgnoreCase(isbn))
                .findFirst()
                .ifPresent(b -> b.setAvailableCopies(b.getAvailableCopies() + 1));

        saveAll();

        return fine > 0
                ? String.format("Return finalized successfully.%n⚠ OVERDUE LATE FINE INCURRED: $ %.2f", fine)
                : "Return finalized successfully. No outstanding fine charges due.";
    }

    /** 
     * Filters active log rows isolating delayed transactions that exceeded their limits.
     */
    public List<Loan> getOverdueLoans() {
        return loans.stream()
                .filter(Loan::isOverdue)
                .collect(Collectors.toList());
    }

    /**
     * Propagates changes across data sinks to guarantee structural balance 
     * (updates inventory indicators and customer string parameters simultaneously).
     */
    private void saveAll() {
        dataManager.saveLoans(loans);
        dataManager.saveBooks(bookService.getBooks());
        dataManager.savePatrons(patronService.getPatrons());
    }
}