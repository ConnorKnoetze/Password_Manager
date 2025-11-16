package Components;

import javax.swing.*;
import java.awt.*;

public class CredentialPanel extends JPanel {
    private final JTextField usernameField;
    private final JTextField passwordField;

    public CredentialPanel(String domain, String username, String password) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder(domain));

        // optional domain label (title already shows domain)
        JLabel domainLabel = new JLabel("Domain: " + domain);
        domainLabel.setFont(domainLabel.getFont().deriveFont(16f));
        domainLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(domainLabel);
        add(Box.createRigidArea(new Dimension(0, 8)));

        // Username row: label + textfield on same horizontal line
        JPanel userRow = new JPanel(new BorderLayout(8, 0));
        userRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(usernameLabel.getFont().deriveFont(16f));
        userRow.add(usernameLabel, BorderLayout.WEST);

        usernameField = new JTextField(username);
        usernameField.setFont(usernameField.getFont().deriveFont(16f));
        usernameField.setEditable(false);
        userRow.add(usernameField, BorderLayout.CENTER);

        // let the row expand horizontally when parent width grows
        Dimension userPref = userRow.getPreferredSize();
        userRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, userPref.height));
        add(userRow);
        add(Box.createRigidArea(new Dimension(0, 8)));

        // Password row: label + textfield on same horizontal line
        JPanel passRow = new JPanel(new BorderLayout(8, 0));
        passRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(passwordLabel.getFont().deriveFont(16f));
        passRow.add(passwordLabel, BorderLayout.WEST);

        passwordField = new JTextField(password);
        passwordField.setFont(passwordField.getFont().deriveFont(16f));
        passwordField.setEditable(false);
        passRow.add(passwordField, BorderLayout.CENTER);

        Dimension passPref = passRow.getPreferredSize();
        passRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, passPref.height));
        add(passRow);
    }

    public void updateCredentials(String username, String password) {
        usernameField.setText(username);
        passwordField.setText(password);
        revalidate();
        repaint();
    }
}
