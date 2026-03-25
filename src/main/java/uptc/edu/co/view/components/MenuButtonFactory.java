package uptc.edu.co.view.components;

import java.awt.Dimension;

import javax.swing.JButton;

public class MenuButtonFactory {
    private static final Dimension BUTTON_SIZE = new Dimension(260, 36);

    public JButton create(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setPreferredSize(BUTTON_SIZE);
        button.addActionListener(event -> action.run());
        return button;
    }
}
