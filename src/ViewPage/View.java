package ViewPage;

import Components.CredentialPanel;
import DomainModel.Credential;
import DomainModel.CredentialsManager;
import DomainModel.Domain;
import DomainModel.DomainsList;
import Scripts.Decryptor;
import Utilities.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class View extends JPanel {
    public View(JsonParser jsonParser) {
        setLayout(new BorderLayout());

        JLabel centerLabel = new JLabel("View Passwords", SwingConstants.CENTER);
        centerLabel.setFont(centerLabel.getFont().deriveFont(18f));
        add(centerLabel, BorderLayout.NORTH);

        JPanel credentialsContainer = new JPanel();
        credentialsContainer.setLayout(new BoxLayout(credentialsContainer, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(credentialsContainer);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        add(scrollPane, BorderLayout.CENTER);

        DomainsList domains = jsonParser.getDomains();
        ArrayList<HashMap<String, String>> jsonList = jsonParser.getJsonList();

        int i = 0;
        for (Domain domain : domains) {
            HashMap<String, String> jsonMap = jsonList.get(i++);

            CredentialPanel credentialPanel = new CredentialPanel(
                    domain.getDomain(),
                    "••••••••••••",
                    "••••••••••••"
            );

            JButton revealBtn = getRevealButton(jsonParser, jsonMap, domain, credentialPanel);

            credentialPanel.add(Box.createRigidArea(new Dimension(0, 6)));
            credentialPanel.add(revealBtn);

            JButton deleteBtn = deleteButton();
            credentialPanel.add(Box.createRigidArea(new Dimension(0, 6)));
            credentialPanel.add(deleteBtn);

            credentialPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            Dimension pref = credentialPanel.getPreferredSize();
            credentialPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));

            credentialsContainer.add(credentialPanel);
            credentialsContainer.add(Box.createRigidArea(new Dimension(0, 8)));
        }
    }

    private JButton deleteButton() {
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setFont(deleteBtn.getFont().deriveFont(14f));
        // Add delete functionality here
        return deleteBtn;
    }

    private JButton getRevealButton(JsonParser jsonParser, HashMap<String, String> jsonMap, Domain domain, CredentialPanel credentialPanel) {
        JButton revealBtn = new JButton("Reveal");
        revealBtn.setFont(revealBtn.getFont().deriveFont(14f));
        revealBtn.addActionListener(e -> {
            revealBtn.setEnabled(false);
            revealBtn.setText("Decrypting...");
            // run decryption off EDT
            SwingWorker<Credential, Void> worker = new SwingWorker<>() {
                @Override
                protected Credential doInBackground() throws Exception {
                    Decryptor decryptor = new Decryptor(jsonParser, new CredentialsManager());
                    try {
                        return decryptor.decryptSingleCredential(jsonMap, domain.getDomain());
                    } catch (IOException ex) {
                        throw ex;
                    }
                }

                @Override
                protected void done() {
                    try {
                        Credential cred = get();
                        credentialPanel.updateCredentials(cred.getUsername(), cred.getPassword());
                        revealBtn.setText("Revealed");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                                SwingUtilities.getWindowAncestor(View.this),
                                "Failed to reveal credential: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                        revealBtn.setText("Reveal");
                    } finally {
                        revealBtn.setEnabled(true);
                    }
                }
            };
            worker.execute();
        });
        return revealBtn;
    }
}