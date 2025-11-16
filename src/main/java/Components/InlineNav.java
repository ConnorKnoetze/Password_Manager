package Components;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class InlineNav extends JPanel {
    private final Map<String, JButton> buttons = new HashMap<>();

    public InlineNav(Consumer<String> navigator, String initialPage) {
        setLayout(new BorderLayout());

        // list holds the buttons stacked vertically
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // wrapper with vertical glue to center the list vertically
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.add(Box.createVerticalGlue());
        wrapper.add(list);
        wrapper.add(Box.createVerticalGlue());

        // place wrapper in center so it sits center-left of the frame
        add(wrapper, BorderLayout.CENTER);

        // prefer a narrow left column
        setPreferredSize(new Dimension(150, 0));

        addNavButton(list, "view", "View", navigator);
        addNavButton(list, "add", "Add", navigator);
        addNavButton(list, "generate", "Generate", navigator);

        setActive(initialPage);
    }

    private void addNavButton(JPanel parent, String key, String label, Consumer<String> navigator) {
        JButton btn = new JButton(label);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.addActionListener(e -> {
            navigator.accept(key);
            setActive(key);
        });
        parent.add(btn);
        parent.add(Box.createVerticalStrut(6));
        buttons.put(key, btn);
    }

    public void setActive(String key) {
        if (key == null) return;
        buttons.forEach((k, b) -> {
            b.setBackground(new Color(0x007ACC));
            b.setForeground(Color.BLACK);
            b.setOpaque(true);
            b.setBorder(UIManager.getBorder("Button.border"));
        });
    }
}