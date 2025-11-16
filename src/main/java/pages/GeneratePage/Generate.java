package pages.GeneratePage;

import javax.swing.*;
import java.awt.*;

import static Utilities.PasswordGenerator.generatePassword;

public class Generate extends JPanel {
    public Generate() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        JLabel titleLabel = new JLabel("Generate A Strong Password");
        titleLabel.setFont(titleLabel.getFont().deriveFont(20f));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        JTextField passwordField = new JTextField();
        passwordField.setFont(passwordField.getFont().deriveFont(16f));
        passwordField.setEditable(false);
        mainPanel.add(passwordField, gbc);

        gbc.gridy = 2;
        gbc.weightx = 1.0;
        JButton generateButton = createGenerateButton(passwordField);
        mainPanel.add(generateButton, gbc);



        add(mainPanel, BorderLayout.CENTER);
    }

    private JButton createGenerateButton(JTextField passwordField) {
        JButton generateButton = new JButton("Generate");
        generateButton.setFont(generateButton.getFont().deriveFont(16f));
        generateButton.addActionListener(e -> {
            String generatedPassword = generatePassword();
            passwordField.setText(generatedPassword);
        });
        return generateButton;
    }
}