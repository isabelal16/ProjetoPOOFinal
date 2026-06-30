import controller.LibraryManager;
import view.LibraryGUI;

import javax.swing.*;
import java.awt.*;

/**
 * Main application entry point executor class.
 * Initializes the backend transactional LibraryManager (prompting raw data file parsing),
 * handles the runtime authentication GUI context window, and shifts interface control 
 * over to the master LibraryGUI terminal frame upon validation success.
 */
public class Main {
    
    public static void main(String[] args) {
        // Enforces thread safety by shifting UI execution to the Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LibraryManager manager = new LibraryManager();
            showLoginScreen(manager);
        });
    }

    /**
     * Renders a modal terminal prompt capture frame handling credential authorization checks.
     */
    private static void showLoginScreen(LibraryManager manager) {
        JTextField txtUser = new JTextField(14);
        JPasswordField txtPass = new JPasswordField(14);

        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        
        panel.add(new JLabel("Username:")); 
        panel.add(txtUser);
        panel.add(new JLabel("Password:")); 
        panel.add(txtPass);
        
        // Guide label showing default hardcoded roles to facilitate user evaluation
        JLabel hint = new JLabel("<html><small><i>admin / admin123 &nbsp;|&nbsp; lib / lib123</i></small></html>");
        hint.setForeground(Color.GRAY);
        panel.add(new JLabel()); 
        panel.add(hint);

        // Infinite context loop until credentials clear authorization or execution halts via termination
        while (true) {
            int opt = JOptionPane.showConfirmDialog(
                    null, 
                    panel,
                    "📚 Session Gateway - Library Terminal",
                    JOptionPane.OK_CANCEL_OPTION, 
                    JOptionPane.PLAIN_MESSAGE
            );
            
            // Wipes execution tracking references if user hits cancel or closes modal contexts
            if (opt != JOptionPane.OK_OPTION) {
                System.exit(0);
            }

            String userText = txtUser.getText().trim();
            String passText = new String(txtPass.getPassword());

            // Fires sequential string evaluation checks against downstream persistence nodes
            if (manager.login(userText, passText)) {
                new LibraryGUI(manager).setVisible(true);
                return; 
            }
            
            // Error handling notification popup logic
            JOptionPane.showMessageDialog(
                    null, 
                    "Invalid username parameters or missing credential matches.",
                    "Authentication Failed", 
                    JOptionPane.ERROR_MESSAGE
            );
            
            txtPass.setText("");
        }
    }
}