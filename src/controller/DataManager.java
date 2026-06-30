package controller;

import model.Book;
import model.Loan;
import model.Patron;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Handles low-level file persistent data streaming routines (File I/O).
 * Segregates parsing operations by structural models, sheltering upper service 
 * logic layers from storage framework dependencies.
 */
public class DataManager {

    private static final String BOOKS_FILE = "books.txt";
    private static final String PATRONS_FILE = "patrons.txt";
    private static final String LOANS_FILE = "loans.txt";
    private static final String LOG_FILE = "overdue_log.txt";

    // --- BOOKS PERSISTENCE ---

    /** 
     * Overwrites flat file outputs with serialized state indicators from book entities.
     */
    public void saveBooks(List<Book> books) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BOOKS_FILE))) {
            for (Book b : books) {
                pw.println(b.toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving books database file: " + e.getMessage());
        }
    }

    /** 
     * Streams local source line definitions converting string components into entities.
     */
    public void loadBooks(List<Book> books) {
        try (Scanner sc = new Scanner(new File(BOOKS_FILE))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] p = line.split(";", -1);
                if (p.length >= 6) {
                    int available = parseInt(p[5]);
                    int total = p.length >= 7 ? parseInt(p[6]) : available;
                    books.add(new Book(p[0], p[1], p[2], p[3], parseInt(p[4]), available, total));
                }
            }
        } catch (FileNotFoundException ignored) {
            // First system launch: safely swallow exception since data files do not exist yet
        } catch (Exception e) {
            System.err.println("Error loading books database file: " + e.getMessage());
        }
    }

    // --- PATRONS PERSISTENCE ---

    /** 
     * Serializes current user definitions to structural flat records.
     */
    public void savePatrons(List<Patron> patrons) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PATRONS_FILE))) {
            for (Patron p : patrons) {
                pw.println(p.toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving patrons database file: " + e.getMessage());
        }
    }

    /** 
     * Materializes character sequences back into functional domain references.
     */
    public void loadPatrons(List<Patron> patrons) {
        try (Scanner sc = new Scanner(new File(PATRONS_FILE))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                // Limit set to 4 components: History tracking strings might hold delimiter paths
                String[] p = line.split(";", 4);
                if (p.length >= 3) {
                    String history = p.length >= 4 ? p[3] : "";
                    patrons.add(new Patron(p[0], p[1], p[2], history));
                }
            }
        } catch (FileNotFoundException ignored) {
            // Safely swallow missing structural components on first boot
        } catch (Exception e) {
            System.err.println("Error loading patrons database file: " + e.getMessage());
        }
    }

    // --- LOANS PERSISTENCE ---

    /** 
     * Registers current transaction tracking rows on target file directories.
     */
    public void saveLoans(List<Loan> loans) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(LOANS_FILE))) {
            for (Loan l : loans) {
                pw.println(l.toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving loans database file: " + e.getMessage());
        }
    }

    /** 
     * Compiles saved transaction details from raw text lines.
     */
    public void loadLoans(List<Loan> loans) {
        try (Scanner sc = new Scanner(new File(LOANS_FILE))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] p = line.split(";", -1);
                if (p.length >= 4) {
                    loans.add(new Loan(p[0], p[1], LocalDate.parse(p[2]), LocalDate.parse(p[3])));
                }
            }
        } catch (FileNotFoundException ignored) {
            // Safely swallow baseline missing exceptions on first launch
        } catch (Exception e) {
            System.err.println("Error loading loans database file: " + e.getMessage());
        }
    }

    // --- REPORTS MANAGEMENT LOG ---

    /** 
     * Commits final compiled administrative metrics text blocks directly into file logs.
     */
    public void saveOverdueLog(String content) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(LOG_FILE, false))) {
            pw.print(content);
        } catch (IOException ignored) { 
            // Suppress unresolvable logging tracking errors
        }
    }

    // --- STRIPPED PARSING UTILITY HELPERS ---

    private int parseInt(String s) {
        try { 
            return Integer.parseInt(s.trim()); 
        } catch (NumberFormatException e) { 
            return 0; 
        }
    }
}