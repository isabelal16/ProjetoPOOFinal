package view;

import controller.LibraryManager;
import model.Loan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

import static view.UIConstants.*;
import static view.UIHelper.*;

/** * 'Reports' Screen Hub processing output summaries, log archiving triggers, and tracking analytics.
 */
public class ReportsPanel extends JPanel {

    private final LibraryManager manager;
    private JTextArea reportArea;

    public ReportsPanel(LibraryManager manager) {
        super(new BorderLayout(0, 8));
        this.manager = manager;
        setBackground(BG);
        setBorder(new EmptyBorder(16, 22, 16, 22));
        buildUI();
    }

    private void buildUI() {
        add(pageHeader("Analytical Summarization Core", new JLabel("Generate reports covering active transactions, financial liabilities, and log audits.")), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setBackground(BG);

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        reportArea.setBackground(PANEL_BG);
        reportArea.setForeground(TEXT);
        reportArea.setBorder(new EmptyBorder(10, 12, 10, 12));

        JButton btnOverdue = accentButton("⚠ Late Item Audits", DANGER);
        JButton btnAll = accentButton("📋 Comprehensive Master Report", ACCENT);
        JButton btnLog = accentButton("💾 Commit Audits To Disk", new Color(0x7C3AED));

        btnOverdue.addActionListener(e -> showOverdueReport());
        btnAll.addActionListener(e -> showAllLoans());
        btnLog.addActionListener(e -> {
            reportArea.setText(manager.generateOverdueLog());
            info(this, "Audit data tracking matrix successfully exported to 'overdue_log.txt'.");
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btnRow.setBackground(BG);
        btnRow.add(btnOverdue); 
        btnRow.add(btnAll); 
        btnRow.add(btnLog);

        center.add(btnRow, BorderLayout.NORTH);
        center.add(new JScrollPane(reportArea), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    private void showOverdueReport() {
        List<Loan> overdue = manager.getOverdueLoans();
        if (overdue.isEmpty()) { 
            reportArea.setText("✅ System check: Green. Zero delayed parameters tracked."); 
            return; 
        }
        StringBuilder sb = new StringBuilder("=== OUTSTANDING OVERDUE MATRICES RECORDED ===\n\n");
        for (Loan l : overdue) {
            manager.getBooks().stream()
                    .filter(b -> b.getIsbn().equalsIgnoreCase(l.getBookIsbn()))
                    .findFirst()
                    .ifPresent(b -> manager.getPatrons().stream()
                            .filter(p -> p.getId().equalsIgnoreCase(l.getPatronId()))
                            .findFirst()
                            .ifPresent(p -> {
                                sb.append("📖 ").append(b.getTitle()).append(" (ISBN Reference: ").append(b.getIsbn()).append(")\n");
                                sb.append("   Account  : ").append(p.getName()).append(" | Destination Path: ").append(p.getContact()).append("\n");
                                sb.append("   Expired  : ").append(l.getDueDate())
                                  .append("   Accrued Fine Balance Liability: $ ").append(String.format("%.2f", l.calculateFine())).append("\n\n");
                            }));
        }
        reportArea.setText(sb.toString());
    }

    private void showAllLoans() {
        List<Loan> loans = manager.getLoans();
        if (loans.isEmpty()) { 
            reportArea.setText("No active transactional workflows logged within current session parameters."); 
            return; 
        }
        StringBuilder sb = new StringBuilder("=== MASTER SYSTEM CIRCULATION LEDGER ===\n\n");
        for (Loan l : loans) {
            String title = manager.getBooks().stream()
                    .filter(b -> b.getIsbn().equalsIgnoreCase(l.getBookIsbn()))
                    .map(model.Book::getTitle)
                    .findFirst()
                    .orElse(l.getBookIsbn());
            
            String name = manager.getPatrons().stream()
                    .filter(p -> p.getId().equalsIgnoreCase(l.getPatronId()))
                    .map(model.Patron::getName)
                    .findFirst()
                    .orElse(l.getPatronId());
            
            sb.append("📖 ").append(title)
              .append(" → Assigned to: ").append(name)
              .append(" | Due Threshold: ").append(l.getDueDate())
              .append(l.isOverdue() ? "  ⚠ OVERDUE CRITICAL ALERT" : "  ✓ Stable").append("\n");
        }
        reportArea.setText(sb.toString());
    }
}