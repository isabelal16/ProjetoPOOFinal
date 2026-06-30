package view;

import controller.LibraryManager;
import exception.LibraryException;
import model.Book;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static view.UIConstants.*;
import static view.UIHelper.*;

/** * 'Books' Dashboard Panel handling catalog lists, custom searching, creation, updates, and records disposal.
 */
public class BooksPanel extends JPanel {

    private final LibraryManager manager;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;
    private JLabel statsLabel;

    public BooksPanel(LibraryManager manager) {
        super(new BorderLayout(0, 8));
        this.manager = manager;
        setBackground(BG);
        setBorder(new EmptyBorder(16, 22, 16, 22));
        buildUI();
        refresh();
    }

    private void buildUI() {
        statsLabel = new JLabel();
        add(pageHeader("Books Catalog", statsLabel), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setBackground(BG);

        // --- Toolbar Query Search Filter Box + Management Actions ---
        JPanel toolRow = new JPanel(new BorderLayout(10, 0));
        toolRow.setBackground(BG);

        searchField = new JTextField();
        styleTextField(searchField, "🔍  Search by title, author, ISBN or genre...");
        searchField.addActionListener(e -> refresh());

        boolean admin = manager.getCurrentUser().isAdmin();
        JButton btnAdd = accentButton("➕ Add Book", ACCENT);
        JButton btnEdit = accentButton("✏ Edit Book", WARNING);
        
        btnAdd.setEnabled(admin);
        btnEdit.setEnabled(admin);
        
        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) { 
                warn(this, "Please select a target row item inside the data catalog view framework."); 
                return; 
            }
            String isbn = (String) tableModel.getValueAt(table.convertRowIndexToModel(viewRow), 1);
            manager.getBooks().stream()
                    .filter(b -> b.getIsbn().equals(isbn))
                    .findFirst()
                    .ifPresent(this::showEditDialog);
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(BG);
        actions.add(btnAdd);
        actions.add(btnEdit);

        toolRow.add(searchField, BorderLayout.CENTER);
        toolRow.add(actions, BorderLayout.EAST);

        // --- Data Presentation Grid Elements Configuration ---
        String[] cols = {"", "ISBN", "Title", "Author", "Genre", "Year", "Available Copies", "Total Copies"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override 
            public Class<?> getColumnClass(int c) {
                if (c == 0) {
                    return Boolean.class;
                }
                if (c == 5 || c == 6 || c == 7) {
                    return Integer.class;
                }
                return String.class;
            }
            @Override 
            public boolean isCellEditable(int r, int c) {
                return c == 0 && manager.getCurrentUser().isAdmin();
            }
        };
        
        table = styledTable(tableModel);
        int[] widths = {34, 90, 220, 160, 100, 50, 80, 60};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        table.getColumnModel().getColumn(0).setMaxWidth(34);

        // --- Bottom Summary Controller Bar: Multiselect Actions + Disposal Procedures ---
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(BG);
        bottomBar.setBorder(new EmptyBorder(6, 2, 0, 2));

        JCheckBox chkAll = new JCheckBox("Select all rows");
        chkAll.setBackground(BG);
        chkAll.setEnabled(admin);
        chkAll.addActionListener(e -> {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(chkAll.isSelected(), i, 0);
            }
        });

        JButton btnDel = accentButton("🗑 Delete Selected Items", DANGER);
        btnDel.setEnabled(admin);
        btnDel.addActionListener(e -> {
            List<String> isbns = new ArrayList<>();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (Boolean.TRUE.equals(tableModel.getValueAt(i, 0))) {
                    isbns.add((String) tableModel.getValueAt(i, 1));
                }
            }
            if (isbns.isEmpty()) { 
                warn(this, "No selection criteria matching rows recorded."); 
                return; 
            }
            if (!confirm(this, "Are you sure you want to drop " + isbns.size() + " book records?")) {
                return;
            }

            List<String> errors = new ArrayList<>();
            for (String isbn : isbns) {
                try { 
                    manager.deleteBook(isbn); 
                } catch (LibraryException ex) { 
                    errors.add(isbn + ": " + ex.getMessage()); 
                }
            }
            chkAll.setSelected(false);
            refresh();
            if (!errors.isEmpty()) {
                error(this, String.join("\n", errors));
            }
        });

        bottomBar.add(chkAll, BorderLayout.WEST);
        bottomBar.add(btnDel, BorderLayout.EAST);

        center.add(toolRow, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        center.add(bottomBar, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);
    }

    private void showAddDialog() {
        JTextField fIsbn = new JTextField(12), fTitle = new JTextField(20);
        JTextField fAuthor = new JTextField(16), fGenre = new JTextField(12);
        JTextField fYear = new JTextField(5), fCopies = new JTextField(4);
        
        JPanel form = formGrid("ISBN:", fIsbn, "Title:", fTitle, "Author:", fAuthor, "Genre:", fGenre, "Year:", fYear, "Initial Copies:", fCopies);
        
        if (JOptionPane.showConfirmDialog(this, form, "Register New Book Entry", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                manager.addBook(new Book(fIsbn.getText().trim(), fTitle.getText().trim(), fAuthor.getText().trim(), fGenre.getText().trim(), intVal(fYear), intVal(fCopies)));
                refresh();
            } catch (Exception ex) { 
                error(this, ex.getMessage()); 
            }
        }
    }

    private void showEditDialog(Book book) {
        JTextField fTitle = new JTextField(book.getTitle(), 20);
        JTextField fAuthor = new JTextField(book.getAuthor(), 16);
        JTextField fGenre = new JTextField(book.getGenre(), 12);
        JTextField fYear = new JTextField(String.valueOf(book.getYear()), 5);
        JTextField fCopies = new JTextField(String.valueOf(book.getTotalCopies()), 4);
        
        JPanel form = formGrid("Title:", fTitle, "Author:", fAuthor, "Genre:", fGenre, "Year:", fYear, "Adjust Total Capacity:", fCopies);
        
        if (JOptionPane.showConfirmDialog(this, form, "Modify Entry Frame - " + book.getIsbn(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                manager.editBook(book.getIsbn(), fTitle.getText().trim(), fAuthor.getText().trim(), fGenre.getText().trim(), intVal(fYear), intVal(fCopies));
                refresh();
            } catch (Exception ex) { 
                error(this, ex.getMessage()); 
            }
        }
    }

    /** * Rebuilds display presentation grids mapping matching user database query updates.
     */
    public void refresh() {
        tableModel.setRowCount(0);
        for (Book b : manager.searchBooks(searchText(searchField))) {
            tableModel.addRow(new Object[]{
                Boolean.FALSE, b.getIsbn(), b.getTitle(), b.getAuthor(), b.getGenre(), b.getYear(), b.getAvailableCopies(), b.getTotalCopies()
            });
        }
        int total = manager.getBooks().size();
        int available = manager.getBooks().stream().mapToInt(Book::getAvailableCopies).sum();
        statsLabel.setText("Total Titles Tracked: " + total + "      |      Net Inventory Available: " + available);
    }
}