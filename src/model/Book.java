package model;

/**
 * Represents a book within the library collection.
 * PRINCIPLE - ENCAPSULATION: Attributes are kept private, with access controlled
 * via explicit getters and setters.
 * Features two constructors: one for registering a brand-new book, and another 
 * for loading existing records from persistent storage.
 */
public class Book {
    private String isbn;
    private String title;
    private String author;
    private String genre;
    private int year;
    private int availableCopies;
    private int totalCopies;

    /** 
     * Constructor used during manual registration.
     * When a book is first added, all copies start as available.
     */
    public Book(String isbn, String title, String author, String genre, int year, int availableCopies) {
        this(isbn, title, author, genre, year, availableCopies, availableCopies);
    }

    /** 
     * Constructor used during file parsing/loading.
     * The number of available and total copies may differ due to active loans.
     */
    public Book(String isbn, String title, String author, String genre, int year, int availableCopies, int totalCopies) {
        this.isbn = isbn; 
        this.title = title; 
        this.author = author;
        this.genre = genre; 
        this.year = year;
        this.availableCopies = availableCopies; 
        this.totalCopies = totalCopies;
    }

    // --- Getters ---
    public String getIsbn() { 
        return isbn; 
    }
    
    public String getTitle() { 
        return title; 
    }
    
    public String getAuthor() { 
        return author; 
    }
    
    public String getGenre() { 
        return genre; 
    }
    
    public int getYear() { 
        return year; 
    }
    
    public int getAvailableCopies() { 
        return availableCopies; 
    }
    
    public int getTotalCopies() { 
        return totalCopies; 
    }

    // --- Setters ---
    public void setTitle(String t) { 
        this.title = t; 
    }
    
    public void setAuthor(String a) { 
        this.author = a; 
    }
    
    public void setGenre(String g) { 
        this.genre = g; 
    }
    
    public void setYear(int y) { 
        this.year = y; 
    }
    
    public void setAvailableCopies(int c) { 
        this.availableCopies = c; 
    }
    
    public void setTotalCopies(int c) { 
        this.totalCopies = c; 
    }

    /** 
     * Converts the object state into a CSV format string for 'books.txt'.
     * Format: isbn;title;author;genre;year;available;total
     */
    @Override
    public String toString() {
        return isbn + ";" + title + ";" + author + ";" + genre + ";" + year + ";" + availableCopies + ";" + totalCopies;
    }

    /** 
     * Performs a case-insensitive search matching the query against
     * the title, author, ISBN, or genre fields.
     */
    public boolean matchesSearch(String query) {
        String q = query.toLowerCase();
        return title.toLowerCase().contains(q) 
                || author.toLowerCase().contains(q)
                || isbn.toLowerCase().contains(q) 
                || genre.toLowerCase().contains(q);
    }
}