import pages.AuthPage.Auth;
import Components.InlineNav;
import Components.Menu;
import DomainModel.CredentialsManager;
import pages.AddPage.Add;
import pages.GeneratePage.Generate;
import Scripts.DataReader;
import Scripts.DataWriter;
import Scripts.Stego;
import pages.ViewPage.View;
import Utilities.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class App extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private boolean authenticated = false;
    private final CredentialsManager credentialsManager;
    private JsonParser jsonParser;
    private Page currentPage = Page.AUTH;
    private static String jsonContents;

    private static final String MASTER_KEY = Stego.extractString();

    public App(){
        DataReader dataReader = new DataReader();
        jsonContents = dataReader.readEncryptedCredentials();
        jsonParser = new JsonParser(jsonContents);
        credentialsManager = new CredentialsManager(jsonParser, MASTER_KEY);
        app();
    }

    private void app() {
        setTitle("Password Manager");
        setIconImage(Toolkit.getDefaultToolkit().getImage(System.getProperty("user.dir")+"\\resources\\key.png"));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);

        // pages as panels (cards)
        Auth authPanel = getAuthPanel();
        cards.add(authPanel, Page.AUTH.getName());

        if (!authenticated) {
            showPage(Page.AUTH.getName());
            JPanel main = new JPanel(new BorderLayout());
            main.add(cards, BorderLayout.CENTER);
            setContentPane(main);
            showPage(Page.AUTH.getName());
        }

        System.out.println("main.App started with CredentialsManager: " + credentialsManager.toString());
    }

    private Auth getAuthPanel() {
        Auth authPanel = new Auth();
        authPanel.addPropertyChangeListener("authenticated", evt -> {
            authenticated = (boolean) evt.getNewValue();
            if (authenticated) {

                DataReader dataReader = new DataReader();
                jsonContents = dataReader.readEncryptedCredentials();
                jsonParser = new JsonParser(jsonContents);

                View viewPanel = new View(credentialsManager, MASTER_KEY);
                viewPanel.addPropertyChangeListener("credentialDeleted", e -> {
                    DataReader dr = new DataReader();
                    jsonParser = new JsonParser(dr.readEncryptedCredentials());
                    viewPanel.reload(credentialsManager);
                });
                cards.add(viewPanel, Page.VIEW.getName());

                Add addPanel = new Add(credentialsManager);
                // IMPORTANT: update in-memory jsonParser immediately when a new credential is added.
                addPanel.addPropertyChangeListener("credentialAdded", e -> {
                    viewPanel.reload(credentialsManager);
                });
                cards.add(addPanel, Page.ADD.getName());
                cards.add(new Generate(), Page.GENERATE.getName());

                System.out.println("User authenticated successfully.");
                showPage(Page.VIEW.getName());
                // create inline nav that calls showPage
                InlineNav nav = new InlineNav(this::showPage, Page.VIEW.getName());

                // main container: nav on the left, cards center
                JPanel main = new JPanel(new BorderLayout());
                main.add(nav, BorderLayout.WEST);
                System.out.println(cardLayout);
                main.add(cards, BorderLayout.CENTER);
                setContentPane(main);

                // menu bar navigator should also update the inline nav highlight
                java.util.function.Consumer<String> navigator = name -> {
                    currentPage = Page.valueOf(name.toUpperCase());
                    showPage(name);
                    nav.setActive(name);
                };

                addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        if (currentPage != Page.AUTH) {
                            executeEncryption();
                        }
                    }
                });

                Menu menu = new Menu(navigator);
                setJMenuBar(menu.getMenuBar());
                showPage(Page.VIEW.getName());
                JButton logoutButton = getLogoutButton();
                main.add(logoutButton, BorderLayout.SOUTH);
                revalidate();
                repaint();
            }
        });
        return authPanel;
    }

    private JButton getLogoutButton() {
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(logoutButton.getFont().deriveFont(16f));
        logoutButton.addActionListener(e -> {
            authenticated = false;
            System.out.println("User logged out.");
            setJMenuBar(null);
            showPage(Page.AUTH.getName());
            JPanel authOnly = new JPanel(new BorderLayout());
            authOnly.add(cards, BorderLayout.CENTER);
            setContentPane(authOnly);

            executeEncryption();
        });

        logoutButton.registerKeyboardAction(actionListener -> logoutButton.doClick(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        return logoutButton;
    }

    public void showPage(String name) {
        cardLayout.show(cards, name);
        try {
            currentPage = Page.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new App().setVisible(true);
        });
    }

    public void executeEncryption(){
        DataWriter dataWriter = new DataWriter();

        dataWriter.writeJson(credentialsManager);
    }

}