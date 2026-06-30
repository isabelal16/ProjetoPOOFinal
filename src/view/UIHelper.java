package view;

import exception.LibraryException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import static view.UIConstants.*;

/**
 * Static layout helpers shared globally by user interface screens.
 * Contains initialization templates for buttons, tables, custom placeholders, inputs, and pop-ups.
 */
public final class UIHelper {
    
    private UIHelper() { }

    // --- Page Header Template ---

    public static JPanel pageHeader(String title, JLabel statsLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(4, 0, 10, 0));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTitle.setForeground(TEXT);

        statsLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        statsLabel.setForeground(MUTED);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(BG);
        left.add(lblTitle);
        left.add(Box.createVerticalStrut(2));
        left.add(statsLabel);

        panel.add(left, BorderLayout.WEST);
        return panel;
    }

    // --- Button Factory ---

    public static JButton accentButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        return btn;
    }

    // --- Text Field Styler and Placeholder Behavior ---

    public static void styleTextField(JTextField field, String placeholder) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCBD5E1)),
                new EmptyBorder(7, 10, 7, 10)));
        field.setForeground(MUTED);
        field.setText(placeholder);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) { 
                    field.setText(""); 
                    field.setForeground(TEXT); 
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) { 
                    field.setText(placeholder); 
                    field.setForeground(MUTED); 
                }
            }
        });
    }

    /** * Extracts user text input, returning an empty string if placeholder fallback is active.
     */
    public static String searchText(JTextField field) {
        return field.getForeground() == MUTED ? "" : field.getText().trim();
    }

    /**
     * Resets field variables back to original baseline placeholder configurations.
     */
    public static void resetField(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(MUTED);
    }

    // --- Input Form Layout Structure ---

    public static JPanel formGrid(Object... pairs) {
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 6));
        p.setBorder(new EmptyBorder(8, 8, 8, 8));
        for (int i = 0; i < pairs.length; i += 2) {
            p.add(new JLabel((String) pairs[i]));
            p.add((Component) pairs[i + 1]);
        }
        return p;
    }

    /**
     * Parses integer context parameters, routing format errors safely through custom system exceptions.
     */
    public static int intVal(JTextField f) throws LibraryException {
        try { 
            return Integer.parseInt(f.getText().trim()); 
        } catch (NumberFormatException e) { 
            throw new LibraryException("Invalid numeric configuration format value: " + f.getText()); 
        }
    }

    // --- Table Decoration Formatting Styles ---

    public static JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setGridColor(new Color(0xE2E8F0));
        table.setBackground(PANEL_BG);
        table.setForeground(TEXT);
        table.setSelectionBackground(new Color(0xBFDBFE));
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 32));
        header.setDefaultRenderer(new HeaderCellRenderer());

        table.setDefaultRenderer(Object.class, new StripedRenderer());
        DefaultTableCellRenderer centered = new StripedRenderer();
        centered.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Integer.class, centered);

        return table;
    }

    /** * Renders alternative table row indicators matching zebra design styles.
     */
    public static class StripedRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? PANEL_BG : ROW_ALT);
            }
            return c;
        }
    }

    /** * Configures custom dark blue background headers handling custom cross-platform Look and Feel layouts.
     */
    public static class HeaderCellRenderer extends DefaultTableCellRenderer {
        public HeaderCellRenderer() { 
            setHorizontalAlignment(SwingConstants.LEFT); 
            setOpaque(true); 
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setBackground(HEADER_BG);
            lbl.setForeground(Color.WHITE);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
            lbl.setBorder(BorderFactory.createCompoundBorder(
                    new MatteBorder(0, 0, 1, 1, new Color(0x16314F)),
                    new EmptyBorder(4, 8, 4, 8)));
            return lbl;
        }
    }

    // --- Standard Notification Dialogue Controllers ---

    public static void info(Component p, String msg) { 
        JOptionPane.showMessageDialog(p, msg, "Information", JOptionPane.INFORMATION_MESSAGE); 
    }
    
    public static void warn(Component p, String msg) { 
        JOptionPane.showMessageDialog(p, msg, "Warning", JOptionPane.WARNING_MESSAGE); 
    }
    
    public static void error(Component p, String msg) { 
        JOptionPane.showMessageDialog(p, msg, "Error", JOptionPane.ERROR_MESSAGE); 
    }
    
    public static boolean confirm(Component p, String msg) { 
        return JOptionPane.showConfirmDialog(p, msg, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION; 
    }
}