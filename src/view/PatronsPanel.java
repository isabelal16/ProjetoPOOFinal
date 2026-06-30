package view;

import controller.LibraryManager;
import exception.LibraryException;
import model.Patron;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static view.UIConstants.*;
import static view.UIHelper.*;

/** * 'Patrons' Management Dashboard supporting filtering pipelines, addition tracking, edits, and archive analysis logs.
 */
public class PatronsPanel extends JPanel {

    private final LibraryManager manager;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;
    private JLabel statsLabel;

    public PatronsPanel(LibraryManager manager) {
        super(new BorderLayout(0, 8));
        this.manager = manager;
        setBackground(BG);
        setBorder(new EmptyBorder(16, 22, 16, 22));
        buildUI();
        refresh();
    }

    private void buildUI() {
        statsLabel = new JLabel();
        add(pageHeader("Patrons Registry", statsLabel), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setBackground(BG);

        JPanel toolRow = new JPanel(new BorderLayout(10, 0));
        toolRow.setBackground(BG);

        searchField = new JTextField();
        styleTextField(searchField, "🔍  Search by member ID or matching name parameters...");
        searchField.addActionListener(e -> refresh());

        boolean admin = manager.getCurrentUser().isAdmin();
        JButton btnAdd = accentButton("➕ Add Profile", ACCENT);
        JButton btnEdit = accentButton("✏ Edit Profile", WARNING);
        JButton btnHist = accentButton("📋 Usage Log", MUTED);
        
        btnAdd.setEnabled(admin);
        btnEdit.setEnabled(admin);

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) { 
                warn(this, "Please select an active member item from the data grid."); 
                return; 
            }
            String id = (String) tableModel.getValueAt(table.convertRowIndexToModel(viewRow), 1);
            manager.getPatrons().stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst()
                    .ifPresent(this::showEditDialog);
        });
        btnHist.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) { 
                warn(this, "Please isolate a targeting user to parse history indicators."); 
                return; 
            }
            String id = (String) tableModel.getValueAt(table.convertRowIndexToModel(viewRow), 1);
            try {
                String hist = manager.getPatronHistory(id);
                JOptionPane.showMessageDialog(this,
                        new JScrollPane(new JTextArea(hist, 6, 40)),
                        "Historical Lending Ledger Log — " + tableModel.getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), 2),
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (LibraryException ex) { 
                error(this, ex.getMessage()); 
            }
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(BG);
        actions.add(btnAdd); 
        actions.add(btnEdit); 
        actions.add(btnHist);

        toolRow.add(searchField, BorderLayout.CENTER);
        toolRow.add(actions, BorderLayout.EAST);

        // --- Grid Struct: checkbox | ID | Name | Contact Info | Logs ---
        String[] cols = {"", "ID", "Full Name", "Contact Path", "Historical Lending Records"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override 
            public Class<?> getColumnClass(int c) { 
                return c == 0 ? Boolean.class : String.class; 
            }
            @Override 
            public boolean isCellEditable(int r, int c) { 
                return c == 0 && manager.getCurrentUser().isAdmin(); 
            }
        };
        
        table = styledTable(tableModel);
        int[] widths = {34, 70, 180, 150, 380};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        table.getColumnModel().getColumn(0).setMaxWidth(34);

        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(BG);
        bottomBar.setBorder(new EmptyBorder(6, 2, 0, 2));

        JCheckBox chkAll = new JCheckBox("Select all users");
        chkAll.setBackground(BG);
        chkAll.setEnabled(admin);
        chkAll.addActionListener(e -> {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(chkAll.isSelected(), i, 0);
            }
        });

        JButton btnDel = accentButton("🗑 Remove Selected Accounts", DANGER);
        btnDel.setEnabled(admin);
        btnDel.addActionListener(e -> {
            List<String> ids = new ArrayList<>();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (Boolean.TRUE.equals(tableModel.getValueAt(i, 0))) {
                    ids.add((String) tableModel.getValueAt(i, 1));
                }
            }
            if (ids.isEmpty()) { 
                warn(this, "Zero target fields isolated for removal handling."); 
                return; 
            }
            if (!confirm(this, "Are you sure you want to wipe " + ids.size() + " profile accounts?")) {
                return;
            }

            List<String> errors = new ArrayList<>();
            for (String id : ids) {
                try { 
                    manager.deletePatron(id); 
                } catch (LibraryException ex) { 
                    errors.add(id + ": " + ex.getMessage()); 
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
        JTextField fId = new JTextField(10), fName = new JTextField(20), fContact = new JTextField(20);
        JPanel form = formGrid("Unique ID:", fId, "Full Name:", fName, "Contact Details:", fContact);
        if (JOptionPane.showConfirmDialog(this, form, "Register Profile Record", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                manager.addPatron(new Patron(fId.getText().trim(), fName.getText().trim(), fContact.getText().trim(), ""));
                refresh();
            } catch (LibraryException ex) { 
                error(this, ex.getMessage()); 
            }
        }
    }

    private void showEditDialog(Patron patron) {
        JTextField fName = new JTextField(patron.getName(), 20), fContact = new JTextField(patron.getContact(), 20);
        JPanel form = formGrid("Name:", fName, "Contact Info:", fContact);
        if (JOptionPane.showConfirmDialog(this, form, "Update Member Sheet — " + patron.getId(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try { 
                manager.editPatron(patron.getId(), fName.getText().trim(), fContact.getText().trim()); 
                refresh(); 
            } catch (LibraryException ex) { 
                error(this, ex.getMessage()); 
            }
        }
    }

    /**
     * Refreshes dashboard rendering lists matching backend operational data streams.
     */
    public void refresh() {
        tableModel.setRowCount(0);
        for (Patron p : manager.searchPatrons(searchText(searchField))) {
            tableModel.addRow(new Object[]{
                Boolean.FALSE, p.getId(), p.getName(), p.getContact(), p.getBorrowingHistory()
            });
        }
        int total = manager.getPatrons().size();
        long activeBorrowersCount = manager.getPatrons().stream()
                .filter(p -> manager.getLoans().stream().anyMatch(l -> l.getPatronId().equalsIgnoreCase(p.getId())))
                .count();
        statsLabel.setText("Total Members Registered: " + total + "      |      With Active Loans Outstanding: " + activeBorrowersCount);
    }
}