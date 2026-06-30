package view;

import controller.LibraryManager;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

import static view.UIConstants.*;

/**
 * Primary Application Window Hub orchestrating Navigation Sidebars and CardLayout content containers.
 * Integrates modular panel fragments (BooksPanel, PatronsPanel, LoansPanel, ReportsPanel).
 */
public class LibraryGUI extends JFrame {

    private final LibraryManager manager;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();

    public LibraryGUI(LibraryManager manager) {
        this.manager = manager;
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception ignored) { 
            // Gracefully fall back to standard basic system styles if platform templates are missing
        }
        buildFrame();
    }

    private void buildFrame() {
        User user = manager.getCurrentUser();
        setTitle("Integrated Management Terminal Core - Account Session: " + user.getUsername() + (user.isAdmin() ? " (Role: Security Admin)" : " (Role: Standard Operator)"));
        setSize(1080, 640);
        setMinimumSize(new Dimension(900, 520));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);

        BooksPanel booksPanel = new BooksPanel(manager);
        PatronsPanel patronsPanel = new PatronsPanel(manager);
        LoansPanel loansPanel = new LoansPanel(manager, booksPanel, patronsPanel);
        ReportsPanel reportsPanel = new ReportsPanel(manager);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG);
        contentPanel.add(booksPanel, "books");
        contentPanel.add(patronsPanel, "patrons");
        contentPanel.add(loansPanel, "loans");
        contentPanel.add(reportsPanel, "reports");

        root.add(contentPanel, BorderLayout.CENTER);
        setContentPane(root);
        setLocationRelativeTo(null);
        showPage("books");
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(HEADER_BG);
        bar.setBorder(new EmptyBorder(10, 18, 10, 18));
        
        JLabel title = new JLabel("📚  Library Management Corporate Suite");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        
        User u = manager.getCurrentUser();
        JLabel userLbl = new JLabel("Active Session: " + u.getUsername() + "  |  Rank Privilege: " + (u.isAdmin() ? "System Administrator" : "Library Desk Operator"));
        userLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        userLbl.setForeground(new Color(0xBFDBFE));
        
        bar.add(title, BorderLayout.WEST);
        bar.add(userLbl, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(190, 0));
        sidebar.setBorder(new EmptyBorder(16, 0, 16, 0));

        addNavButton(sidebar, "books", "📖  Inventory Matrix");
        addNavButton(sidebar, "patrons", "👤  Member Accounts");
        addNavButton(sidebar, "loans", "🔄  Lending Routines");
        addNavButton(sidebar, "reports", "📊  Audit Reports");

        sidebar.add(Box.createVerticalGlue());
        JButton btnExit = navButton("🚪  Terminate Session");
        btnExit.addActionListener(e -> System.exit(0));
        sidebar.add(btnExit);
        sidebar.add(Box.createVerticalStrut(10));
        return sidebar;
    }

    private void addNavButton(JPanel sidebar, String key, String label) {
        JButton btn = navButton(label);
        btn.addActionListener(e -> showPage(key));
        navButtons.put(key, btn);
        sidebar.add(btn);
    }

    private JButton navButton(String label) {
        JButton btn = new JButton(label);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(SIDEBAR_BG);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 10));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { 
                if (btn.getBackground() != SIDEBAR_ACT) {
                    btn.setBackground(SIDEBAR_HOV); 
                }
            }
            @Override
            public void mouseExited(MouseEvent e) { 
                if (btn.getBackground() != SIDEBAR_ACT) {
                    btn.setBackground(SIDEBAR_BG); 
                }
            }
        });
        return btn;
    }

    private void showPage(String key) {
        cardLayout.show(contentPanel, key);
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            boolean active = entry.getKey().equals(key);
            entry.getValue().setBackground(active ? SIDEBAR_ACT : SIDEBAR_BG);
            entry.getValue().setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 14));
        }
    }
}