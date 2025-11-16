package Pages.AddPage;

import DomainModel.Credential;
import DomainModel.CredentialsManager;
import DomainModel.Domain;
import Utilities.PasswordGenerator;

import javax.swing.*;
import java.awt.*;

public class Add extends JPanel {

    public Add(CredentialsManager credentialsManager) {
        setLayout(new BorderLayout());
        // Form panel with labels and fields
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Domain
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        form.add(new JLabel("Domain:"), gbc);
        JTextField domainField = new JTextField();
        domainField.setFont(domainField.getFont().deriveFont(16f));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        form.add(domainField, gbc);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        form.add(new JLabel("Username:"), gbc);

        JTextField usernameField = new JTextField();
        usernameField.setFont(usernameField.getFont().deriveFont(16f));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        form.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        form.add(new JLabel("Password:"), gbc);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(passwordField.getFont().deriveFont(16f));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        form.add(passwordField, gbc);

        form.add(createGenerateButton(gbc, passwordField), gbc);

        // Response
        JLabel responseField = new JLabel();
        responseField.setFont(responseField.getFont().deriveFont(16f));
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        form.add(responseField, gbc);
        responseField.setText(" ");



        add(form, BorderLayout.CENTER);

        // Bottom button area
        JButton addButton = new JButton("Add New Password");
        addButton.setFont(addButton.getFont().deriveFont(16f));
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        bottom.add(addButton);
        add(bottom, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            String domain = domainField.getText().strip();
            String username = usernameField.getText().strip();
            String password = new String(passwordField.getPassword()).strip();

            // validate inputs
            if (domain.isEmpty() || username.isEmpty() || password.isEmpty()) {
                responseField.setText("Please fill in all fields.");
                return;
            }
            // Handle add button action
            Credential credential = new Credential(
                    new Domain(domain),
                    username,
                    password
            );

            // Clear fields after adding
            domainField.setText("");
            usernameField.setText("");
            passwordField.setText("");

            credentialsManager.addCredential(credential);
            responseField.setText(String.format("Credential for %s added successfully.", credential.getDomain()));
            firePropertyChange("credentialAdded", null, credential);
        });
    }

    public JButton createGenerateButton(GridBagConstraints gbc, JPasswordField passwordField){
        JButton generateButton = new JButton();
        generateButton.setFont(generateButton.getFont().deriveFont(16f));
        generateButton.setText("Generate Password");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.0;

        generateButton.addActionListener( e -> {
            String generatedPass = PasswordGenerator.generatePassword();
            passwordField.setText(generatedPass);
            passwordField.setEchoChar((char) 0);
        });

        return generateButton;
    }
}