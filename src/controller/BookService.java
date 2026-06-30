package controller;

import exception.LibraryException;
import model.Book;
import model.Loan;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles all core operations related to BOOKS: creation, updates, deletions, and searches.
 * Maintains its own local instance registry tracking the catalog database records.
 */
public class BookService {

    private final List<Book> books = new ArrayList<>();
    private final AuthenticationService auth;
    private final DataManager dataManager;

    public BookService(AuthenticationService auth, DataManager dataManager) {
        this.auth = auth;
        this.dataManager = dataManager;
        dataManager.loadBooks(books); // Loads existing file data upon system initialization
    }

    /** 
     * Retrives the current live internal database catalog records.
     */
    public List<Book> getBooks() { 
        return books; 
    }

    /**
     * Registers a new book into the repository catalog.
     * Restricted to Admins. Validates uniqueness to prevent ISBN duplications.
     */
    public void addBook(Book book) throws LibraryException {
        auth.requireAdmin();
        for (Book b : books) {
            if (b.getIsbn().equalsIgnoreCase(book.getIsbn())) {
                throw new LibraryException("ISBN already registered: " + book.getIsbn());
            }
        }
        books.add(book);
        dataManager.saveBooks(books);
    }

    /**
     * Updates key fields of an existing book record.
     * Enforces business rule: The updated total copy capacity cannot be adjusted lower 
     * than the aggregate number of books currently checked out by active patrons.
     */
    public void editBook(String isbn, String newTitle, String newAuthor, String newGenre, int newYear, int newTotalCopies) throws LibraryException {
        auth.requireAdmin();
        Book book = findBook(isbn);
        
        int loaned = book.getTotalCopies() - book.getAvailableCopies();
        if (newTotalCopies < loaned) {
            throw new LibraryException("Total copies cannot be lower than active checked-out loans (" + loaned + ").");
        }
        
        book.setTitle(newTitle);
        book.setAuthor(newAuthor);
        book.setGenre(newGenre);
        book.setYear(newYear);
        book.setAvailableCopies(newTotalCopies - loaned);
        book.setTotalCopies(newTotalCopies);
        
        dataManager.saveBooks(books);
    }

    /**
     * Erases a book from the active system index.
     * Data Integrity Constraint: Prevents deletion if active loans match the target ISBN, 
     * shielding the application from orphan references.
     */
    public void deleteBook(String isbn, List<Loan> loans) throws LibraryException {
        auth.requireAdmin();
        for (Loan l : loans) {
            if (l.getBookIsbn().equalsIgnoreCase(isbn)) {
                throw new LibraryException("Cannot delete a book record with active loans pending return.");
            }
        }
        if (!books.removeIf(b -> b.getIsbn().equalsIgnoreCase(isbn))) {
            throw new LibraryException("Book record not found for ISBN: " + isbn);
        }
        dataManager.saveBooks(books);
    }

    /** 
     * Filters the catalog database by comparing a query phrase against searchable metadata fields.
     */
    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(books);
        }
        return books.stream()
                .filter(b -> b.matchesSearch(query))
                .collect(Collectors.toList());
    }

    /** 
     * Safely isolates individual items by matching unique primary identifier tokens.
     */
    public Book findBook(String isbn) throws LibraryException {
        return books.stream()
                .filter(b -> b.getIsbn().equalsIgnoreCase(isbn))
                .findFirst()
                .orElseThrow(() -> new LibraryException("Book record not found for ISBN: " + isbn));
    }
}