import AuthPage.Auth;
import Components.InlineNav;
import Components.Menu;
import DomainModel.CredentialsManager;
import HomePage.Home;
import AddPage.Add;
import GeneratePage.Generate;
import Scripts.DataReader;
import Scripts.DataWriter;
import Scripts.Decryptor;
import ViewPage.View;
import Utilities.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.*;

public class App extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private boolean authenticated = false;
    private static final CredentialsManager credentialsManager = new CredentialsManager();
    private JsonParser jsonParser;

    private Page currentPage = Page.AUTH;

    private final Page[] pages = Page.values();

    private static String jsonContents;

    public App(){
        DataReader dataReader = new DataReader();
        jsonContents = dataReader.readEncryptedCredentials();
        jsonParser = new JsonParser(jsonContents);
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

        // window close cleanup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Application is closing. Cleaning up resources...");
                if (currentPage != Page.AUTH) {
                    executeEncryption(true);
                }
            }
        });

        System.out.println("App started with CredentialsManager: " + credentialsManager.toString());
    }

    private Auth getAuthPanel() {
        Auth authPanel = new Auth();
        authPanel.addPropertyChangeListener("authenticated", evt -> {

            authenticated = (boolean) evt.getNewValue();
            if (authenticated) {
                DataReader dataReader = new DataReader();
                jsonContents = dataReader.readEncryptedCredentials();
                jsonParser = new JsonParser(jsonContents);

                cards.add(new Home(), Page.HOME.getName());
                cards.add(new View(jsonParser), Page.VIEW.getName());
                Add addPanel = new Add(credentialsManager);
                addPanel.addPropertyChangeListener("credentialAdded", e -> {
                    System.out.println("New credential added: " + credentialsManager.toString());
                });
                cards.add(addPanel, Page.ADD.getName());
                cards.add(new Generate(), Page.GENERATE.getName());

                System.out.println("User authenticated successfully.");
                showPage(Page.HOME.getName());
                // create inline nav that calls showPage
                InlineNav nav = new InlineNav(this::showPage, Page.HOME.getName());

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

                Menu menu = new Menu(navigator);
                setJMenuBar(menu.getMenuBar());
                showPage(Page.HOME.getName());
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

            executeEncryption(true);
        });

        logoutButton.registerKeyboardAction(actionListener -> logoutButton.doClick(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        return logoutButton;
    }

    public void showPage(String name) {
        cardLayout.show(cards, name);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new App().setVisible(true);
        });
    }

    private void decryptCredentialsAsync(Decryptor decryptor) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            try {
                decryptor.decryptAllCredentials();
            } catch (IOException e) {
                System.err.println("Failed to decrypt credentials: " + e.getMessage());
            }
        });
    }

    private void executeEncryption(boolean logout) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            DataWriter dataWriter = new DataWriter(credentialsManager);
            if (credentialsManager.isEmpty()) {
                System.out.println("No credentials to save.");
                dataWriter.writeJson(jsonParser);
                return;
            }
            try {
                jsonContents = dataWriter.EncryptCredentials(jsonContents, jsonParser);
                dataWriter.writeCipherTexts();
            } catch (Exception ex) {
                System.err.println("Cleanup task failed: " + ex.getMessage());
            }
            System.out.println("Cleanup completed.");
        });
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                future.cancel(true);
                executor.shutdownNow();
                System.err.println("Cleanup timed out; forcing exit.");
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        } finally {
            credentialsManager.clearCredentials();
            if (!logout) {dispose();System.exit(0);}
        }
    }
}