package pages.AuthPage;

import javax.swing.*;
import java.awt.*;

public class Auth extends JPanel {
    private static final String PASSWORD = "password123"; // Example password
    public Auth() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 20, 8, 20);

        String username = System.getProperty("user.name");

        JLabel centerLabel = new JLabel(String.format("Hello %s, Please Enter Password", username), SwingConstants.CENTER);
        centerLabel.setFont(centerLabel.getFont().deriveFont(18f));
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        add(centerLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(passwordField.getFont().deriveFont(16f));
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        add(passwordField, gbc);
        passwordField.setText("password123");

        gbc.gridy = 2;
        gbc.gridwidth = 1;

        JButton loginButton = getLoginButton(passwordField, centerLabel, username);
        gbc.gridx = 0;
        gbc.weightx = 0.8;
        add(loginButton, gbc);

        JButton showPasswordButton = getShowPasswordButton(passwordField);
        gbc.gridx = 1;
        gbc.weightx = 0.2;
        add(showPasswordButton, gbc);

        registerKeyboardAction(actionListener -> loginButton.doClick(),
                KeyStroke.getKeyStroke("ENTER"),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private static JButton getShowPasswordButton(JPasswordField passwordField) {
        JButton showPasswordButton = new JButton("Show Password");
        showPasswordButton.setFont(showPasswordButton.getFont().deriveFont(16f));
        showPasswordButton.addActionListener(e -> {
            if (passwordField.getEchoChar() != (char) 0) {
                passwordField.setEchoChar((char) 0);
                showPasswordButton.setText("Hide Password");
            } else {
                passwordField.setEchoChar('\u2022');
                showPasswordButton.setText("Show Password");
            }
        });
        return showPasswordButton;
    }

    private JButton getLoginButton(JPasswordField passwordField, JLabel centerLabel, String username) {
        JButton loginButton = new JButton("Login");
        loginButton.setFont(loginButton.getFont().deriveFont(16f));
        loginButton.addActionListener(
            e -> {
                String enteredPassword = new String(passwordField.getPassword());
                if (PASSWORD.equals(enteredPassword)) {
                    firePropertyChange("authenticated", false, true);
                    passwordField.setText("");
                    centerLabel.setText(String.format("Hello %s, Please Enter Password", username));
                } else {
                    centerLabel.setText("Incorrect Password. Try Again.");
                    passwordField.setText("");
                }
            }
        );
        return loginButton;
    }

}
