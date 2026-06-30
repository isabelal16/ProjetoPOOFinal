package controller;

import exception.LibraryException;
import model.Book;
import model.Loan;
import model.Patron;
import model.User;

import java.util.List;

/**
 * Structural FACADE PATTERN design gateway providing unified system control.
 *
 * Graphical interfaces (Panels) interface EXCLUSIVELY through this controller hook. 
 * Incoming calls translate directly downwards to specific isolated domain tracking services:
 *
 *   AuthenticationService -> Sign-ins, tracking sessions, access authorization
 *   BookService           -> Inventory data manipulation mapping (CRUD)
 *   PatronService         -> Profile record manipulation mapping (CRUD)
 *   LoanService           -> Transaction checkouts and returns processing
 *   ReportService         -> Formatting text outputs and recording overdue histories
 *   DataManager           -> Low-level persistent flat file management orchestration
 */
public class LibraryManager {

    private final DataManager dataManager;
    private final AuthenticationService auth;
    private final BookService bookService;
    private final PatronService patronService;
    private final LoanService loanService;
    private final ReportService reportService;

    public LibraryManager() {
        dataManager = new DataManager();
        auth = new AuthenticationService();

        // Dependencies are initialized sequentially to preserve dependency injection flow logic
        bookService = new BookService(auth, dataManager);
        patronService = new PatronService(auth, dataManager);
        loanService = new LoanService(bookService, patronService, dataManager);
        reportService = new ReportService(bookService, patronService, loanService, dataManager);
    }

    // --- Authentication Actions ---
    
    public boolean login(String username, String password) { 
        return auth.login(username, password); 
    }
    
    public User getCurrentUser() { 
        return auth.getCurrentUser(); 
    }

    // --- Book Records Operations ---
    
    public void addBook(Book book) throws LibraryException { 
        bookService.addBook(book); 
    }
    
    public void editBook(String isbn, String t, String a, String g, int y, int c) throws LibraryException { 
        bookService.editBook(isbn, t, a, g, y, c); 
    }
    
    public void deleteBook(String isbn) throws LibraryException { 
        bookService.deleteBook(isbn, loanService.getLoans()); 
    }
    
    public List<Book> searchBooks(String query) { 
        return bookService.searchBooks(query); 
    }
    
    public List<Book> getBooks() { 
        return bookService.getBooks(); 
    }

    // --- Patron Profiles Operations ---
    
    public void addPatron(Patron patron) throws LibraryException { 
        patronService.addPatron(patron); 
    }
    
    public void editPatron(String id, String name, String contact) throws LibraryException { 
        patronService.editPatron(id, name, contact); 
    }
    
    public void deletePatron(String id) throws LibraryException { 
        patronService.deletePatron(id, loanService.getLoans()); 
    }
    
    public List<Patron> searchPatrons(String query) { 
        return patronService.searchPatrons(query); 
    }
    
    public List<Patron> getPatrons() { 
        return patronService.getPatrons(); 
    }
    
    public String getPatronHistory(String patronId) throws LibraryException { 
        return patronService.getPatronHistory(patronId); 
    }

    // --- Transaction Lending Workflows ---
    
    public void checkoutBook(String isbn, String patronId) throws LibraryException { 
        loanService.checkoutBook(isbn, patronId); 
    }
    
    public String returnBook(String isbn, String patronId) throws LibraryException { 
        return loanService.returnBook(isbn, patronId); 
    }
    
    public List<Loan> getLoans() { 
        return loanService.getLoans(); 
    }
    
    public List<Loan> getOverdueLoans() { 
        return loanService.getOverdueLoans(); 
    }

    // --- System Reports Handling ---
    
    public String generateOverdueLog() { 
        return reportService.generateOverdueLog(); 
    }
}