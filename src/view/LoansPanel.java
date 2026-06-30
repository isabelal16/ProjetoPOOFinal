package view;

import controller.LibraryManager;
import exception.LibraryException;
import model.Book;
import model.Loan;
import model.Patron;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import static view.UIConstants.*;
import static view.UIHelper.*;

/**
 * 'Loans' Dashboard Panel orchestrating transaction checkouts, item returns, and delayed trackers.
 * * DESIGN ACCESSIBILITY: Cross-references keys displaying explicit human-readable variables 
 * (such as BOOK TITLES and PATRON NAMES) inside rows, bypassing simple technical tokens.
 */
public class LoansPanel extends JPanel {

    private final LibraryManager manager;
    private final BooksPanel booksPanel;
    private final PatronsPanel patronsPanel;

    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel statsLabel;

    public LoansPanel(LibraryManager manager, BooksPanel booksPanel, PatronsPanel patronsPanel) {
        super(new BorderLayout(0, 8));
        this.manager = manager;
        this.booksPanel = booksPanel;
        this.patronsPanel = patronsPanel;
        setBackground(BG);
        setBorder(new EmptyBorder(16, 22, 16, 22));
        buildUI();
        refresh();
    }

    private void buildUI() {
        statsLabel = new JLabel();
        add(pageHeader("Transactional Circulation Ledger", statsLabel), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setBackground(BG);

        // --- Checkout / Return Form Row Elements ---
        JTextField fIsbn = new JTextField(10);
        JTextField fPatronId = new JTextField(8);
        styleTextField(fIsbn, "Target ISBN");
        styleTextField(fPatronId, "Target Patron ID");

        JButton btnCheckout = accentButton("📤 Checkout Item", ACCENT2);
        JButton btnReturn = accentButton("📥 Process Return", ACCENT);

        btnCheckout.addActionListener(e -> {
            try {
                manager.checkoutBook(searchText(fIsbn), searchText(fPatronId));
                refresh();
                booksPanel.refresh();
                patronsPanel.refresh();
                resetField(fIsbn, "Target ISBN");
                resetField(fPatronId, "Target Patron ID");
                info(this, "Checkout transaction registered successfully.");
            } catch (LibraryException ex) { 
                error(this, ex.getMessage()); 
            }
        });

        btnReturn.addActionListener(e -> {
            try {
                String msg = manager.returnBook(searchText(fIsbn), searchText(fPatronId));
                refresh();
                booksPanel.refresh();
                patronsPanel.refresh();
                resetField(fIsbn, "Target ISBN");
                resetField(fPatronId, "Target Patron ID");
                info(this, msg);
            } catch (LibraryException ex) { 
                error(this, ex.getMessage()); 
            }
        });

        JPanel formRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        formRow.setBackground(BG);
        formRow.add(new JLabel("ISBN:")); 
        formRow.add(fIsbn);
        formRow.add(new JLabel("Patron ID:")); 
        formRow.add(fPatronId);
        formRow.add(btnCheckout);
        formRow.add(btnReturn);

        // --- Active Loans Tracking Presentation Grid ---
        String[] cols = {"ISBN", "Target Book Title", "Patron ID", "Full Legal Name", "Checkout Date", "Expected Due Date", "System Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override 
            public boolean isCellEditable(int r, int c) { 
                return false; 
            }
        };
        table = styledTable(tableModel);

        int[] widths = {80, 200, 80, 160, 110, 130, 100};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Overdue cell entries switch background dynamically to alert users
        table.setDefaultRenderer(Object.class, new StripedRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                int modelRow = t.convertRowIndexToModel(row);
                String status = (String) tableModel.getValueAt(modelRow, 6);
                if (!isSelected && "OVERDUE LATE".equals(status)) {
                    c.setBackground(new Color(0xFEE2E2));
                    c.setForeground(DANGER);
                } else if (!isSelected) {
                    c.setForeground(TEXT);
                }
                return c;
            }
        });

        center.add(formRow, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    /**
     * Re-aggregates structural objects to generate human-readable description panels across data lines.
     */
    public void refresh() {
        tableModel.setRowCount(0);
        for (Loan l : manager.getLoans()) {

            // Translates technical primary keys into legible titles via functional programming filters
            String bookTitle = manager.getBooks().stream()
                    .filter(b -> b.getIsbn().equalsIgnoreCase(l.getBookIsbn()))
                    .map(Book::getTitle)
                    .findFirst()
                    .orElse("(Unknown Source)");

            String patronName = manager.getPatrons().stream()
                    .filter(p -> p.getId().equalsIgnoreCase(l.getPatronId()))
                    .map(Patron::getName)
                    .findFirst()
                    .orElse("(Unknown Patron Profile)");

            String status = l.isOverdue() ? "OVERDUE LATE" : "Active In-Term";

            tableModel.addRow(new Object[]{
                l.getBookIsbn(), bookTitle, l.getPatronId(), patronName, l.getLoanDate(), l.getDueDate(), status
            });
        }

        long total = manager.getLoans().size();
        long overdue = manager.getOverdueLoans().size();
        statsLabel.setText("Aggregate Active Outstanding Items: " + total + "      |      Total Delayed Lines: " + overdue);
    }
}