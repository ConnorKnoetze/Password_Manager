package ViewPage;

import Components.CredentialPanel;
import DomainModel.Credential;
import DomainModel.CredentialsManager;
import DomainModel.Domain;
import Scripts.DataWriter;
import Scripts.Decryptor;
import Utilities.Search;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class View extends JPanel {
    private CredentialsManager credentialsManager;
    private final JPanel credentialsContainer;
    private static String MASTER_KEY;

    public View(CredentialsManager credentialsManager, String masterKey) {
        MASTER_KEY = masterKey;
        this.credentialsManager = credentialsManager;
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        JLabel centerLabel = new JLabel("View Passwords", SwingConstants.CENTER);
        centerLabel.setFont(centerLabel.getFont().deriveFont(18f));
        topPanel.add(centerLabel, BorderLayout.NORTH);


        JPanel searchPanel = getSearchPanel(this, credentialsManager);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        credentialsContainer = new JPanel();
        credentialsContainer.setLayout(new BoxLayout(credentialsContainer, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(credentialsContainer);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        add(scrollPane, BorderLayout.CENTER);

        buildCredentials(credentialsManager.getDomains(), credentialsManager.getJsonList());
    }

    private static JPanel getSearchPanel(View view, CredentialsManager credentialsManager){
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField searchTextField = new JTextField();
        searchTextField.setFont(searchTextField.getFont().deriveFont(16f));
        gbc.gridx = 0;
        gbc.weightx = 0.95;
        searchPanel.add(searchTextField, gbc);

        JButton searchButton = new JButton("Search");
        searchButton.setFont(searchTextField.getFont().deriveFont(18f));
        gbc.gridx = 1;
        gbc.weightx = 0.05;
        searchButton.addActionListener(e -> {
            String query = searchTextField.getText();
            if (query.equalsIgnoreCase("")){
                view.buildCredentials(credentialsManager.getDomains(), credentialsManager.getJsonList());
                return;
            }
            Search.search(credentialsManager, query);
            for(Domain domain : credentialsManager.getSearchedDomains()){
                System.out.println(domain.getDomain());
                view.buildCredentials(credentialsManager.getSearchedDomains(), credentialsManager.getSearchedJsonList());
            }
        });

        searchButton.registerKeyboardAction(actionListener -> searchButton.doClick(),
                KeyStroke.getKeyStroke("ENTER"),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        searchPanel.add(searchButton, gbc);

        return searchPanel;
    }

    private void buildCredentials(ArrayList<Domain> domains, ArrayList<HashMap<String, String>> jsonList) {
        credentialsContainer.removeAll();

        int i = 0;
        for (Domain domain : domains) {
            HashMap<String, String> jsonMap = jsonList.get(i++);

            CredentialPanel credentialPanel = new CredentialPanel(
                    domain.getDomain(),
                    "Encrypted",
                    "Encrypted"
            );

            JButton revealBtn = getRevealButton(jsonMap, domain, credentialPanel);

            credentialPanel.add(Box.createRigidArea(new Dimension(0, 6)));
            credentialPanel.add(revealBtn);

            JButton deleteBtn = deleteButton(credentialsManager, domain);
            credentialPanel.add(Box.createRigidArea(new Dimension(0, 6)));
            credentialPanel.add(deleteBtn);

            credentialPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            Dimension pref = credentialPanel.getPreferredSize();
            credentialPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));

            credentialsContainer.add(credentialPanel);
            credentialsContainer.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        revalidate();
        repaint();
    }

    private JButton deleteButton(CredentialsManager credentialsManager, Domain domain) {
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setFont(deleteBtn.getFont().deriveFont(14f));

        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    SwingUtilities.getWindowAncestor(View.this),
                    "Are you sure you want to delete the credential for " + domain.getDomain() + "?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                credentialsManager.deleteCredential(domain.getDomain());
                DataWriter dataWriter = new DataWriter();
                dataWriter.writeJson(credentialsManager);
                firePropertyChange("credentialDeleted", false, true);
            }
        });

        return deleteBtn;
    }

    private JButton getRevealButton(HashMap<String, String> jsonMap, Domain domain, CredentialPanel credentialPanel) {
        JButton revealBtn = new JButton("Reveal");
        revealBtn.setFont(revealBtn.getFont().deriveFont(14f));
        revealBtn.addActionListener(e -> {
            revealBtn.setEnabled(false);
            revealBtn.setText("Decrypting...");
            SwingWorker<Credential, Void> worker = new SwingWorker<>() {
                @Override
                protected Credential doInBackground() throws Exception {
                    Decryptor decryptor = new Decryptor(MASTER_KEY);
                    return decryptor.decryptSingleCredential(jsonMap, domain.getDomain());
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

    public void reload(CredentialsManager credentialsManager) {
        this.credentialsManager = credentialsManager;
        Runnable doBuiltCredentials = () -> buildCredentials(credentialsManager.getDomains(), credentialsManager.getJsonList());
        SwingUtilities.invokeLater(doBuiltCredentials);
    }
}