package HomePage;

import javax.swing.*;
import java.awt.*;

public class Home extends JPanel {
    public Home() {
        setLayout(new BorderLayout());
        JLabel centerLabel = new JLabel("Home", SwingConstants.CENTER);
        centerLabel.setFont(centerLabel.getFont().deriveFont(18f));
        add(centerLabel, BorderLayout.CENTER);
    }
}