package uptc.edu.co.view.components;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class VerticalMenuPanel extends JPanel {

    public VerticalMenuPanel(String titleText, JButton[] buttons, int verticalPadding) {
        setLayout(new BorderLayout());
        add(new JLabel(titleText, SwingConstants.CENTER), BorderLayout.NORTH);
        add(createCenteredButtons(buttons, verticalPadding), BorderLayout.CENTER);
    }

    private JPanel createCenteredButtons(JButton[] buttons, int verticalPadding) {
        JPanel grid = new JPanel(new GridLayout(buttons.length, 1, 12, 12));
        for (int index = 0; index < buttons.length; index++) {
            grid.add(buttons[index]);
        }
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, verticalPadding));
        center.add(grid);
        return center;
    }
}
