package Components;

import javax.swing.*;
import java.util.function.Consumer;

public class Menu {
    private JMenuBar menuBar;

    public Menu(Consumer<String> navigator) {
        menu(navigator);
    }

    private void menu(Consumer<String> navigator) {
        menuBar = new JMenuBar();
        fileMenu();
        pagesMenu(navigator);
    }

    private void pagesMenu(Consumer<String> navigator) {
        JMenu pagesMenu = new JMenu("Pages");
        JMenuItem homeItem = new JMenuItem("Home");
        JMenuItem viewItem = new JMenuItem("View Passwords");
        JMenuItem addItem = new JMenuItem("Add Password");
        JMenuItem generateItem = new JMenuItem("Generate Password");

        homeItem.addActionListener(e -> navigator.accept("home"));
        viewItem.addActionListener(e -> navigator.accept("view"));
        addItem.addActionListener(e -> navigator.accept("add"));
        generateItem.addActionListener(e -> navigator.accept("generate"));

        pagesMenu.add(homeItem);
        pagesMenu.add(viewItem);
        pagesMenu.add(addItem);
        pagesMenu.add(generateItem);
        menuBar.add(pagesMenu);
    }

    private void fileMenu() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }
}