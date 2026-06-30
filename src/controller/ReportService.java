package controller;

import model.Book;
import model.Loan;
import model.Patron;

import java.time.LocalDate;
import java.util.List;

/**
 * Handles the compilation and rendering of structured data summaries.
 * Isolated from LoanService to respect Separation of Concerns (SoC): 
 * it aggregates cross-entity information to generate localized text layout strings.
 */
public class ReportService {

    private final BookService bookService;
    private final PatronService patronService;
    private final LoanService loanService;
    private final DataManager dataManager;

    public ReportService(BookService bookService, PatronService patronService, LoanService loanService, DataManager dataManager) {
        this.bookService = bookService;
        this.patronService = patronService;
        this.loanService = loanService;
        this.dataManager = dataManager;
    }

    /**
     * Compiles detailed transaction lines focusing on delayed item transactions, 
     * binding books, borrower contact info, target deadline parameters, and outstanding balances.
     * 
     * Writes summaries to local disk outputs ('overdue_log.txt') fulfilling specific project specs:
     * "Email-style reminder messages written to a file/log".
     * 
     * @return the formatted report layout contents.
     */
    public String generateOverdueLog() {
        List<Loan> overdue = loanService.getOverdueLoans();
        StringBuilder sb = new StringBuilder();
        sb.append("=== OVERDUE LOANS REPORT - ").append(LocalDate.now()).append(" ===\n\n");

        if (overdue.isEmpty()) {
            sb.append("No overdue loans recorded in the system.\n");
        } else {
            for (Loan l : overdue) {
                // cross-references tracking indicators to parse rich human-readable metadata structures
                findBook(l.getBookIsbn(), bookService.getBooks()).ifPresent(b -> 
                    findPatron(l.getPatronId(), patronService.getPatrons()).ifPresent(p -> {
                        sb.append("Book Title : ").append(b.getTitle())
                          .append(" (ISBN: ").append(b.getIsbn()).append(")\n");
                        sb.append("Patron     : ").append(p.getName())
                          .append(" | Contact: ").append(p.getContact()).append("\n");
                        sb.append("Due Date   : ").append(l.getDueDate())
                          .append(" | Accrued Fine: $ ")
                          .append(String.format("%.2f", l.calculateFine())).append("\n\n");
                    })
                );
            }
        }

        String content = sb.toString();
        dataManager.saveOverdueLog(content);
        return content;
    }

    // --- Private Internal Utility Helpers ---

    private java.util.Optional<Book> findBook(String isbn, List<Book> books) {
        return books.stream()
                .filter(b -> b.getIsbn().equalsIgnoreCase(isbn))
                .findFirst();
    }

    private java.util.Optional<Patron> findPatron(String id, List<Patron> patrons) {
        return patrons.stream()
                .filter(p -> p.getId().equalsIgnoreCase(id))
                .findFirst();
    }
}