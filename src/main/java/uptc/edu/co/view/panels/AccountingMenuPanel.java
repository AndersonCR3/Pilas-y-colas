package uptc.edu.co.view.panels;

import javax.swing.JButton;

import uptc.edu.co.i18n.MessageService;
import uptc.edu.co.view.components.MenuButtonFactory;
import uptc.edu.co.view.components.VerticalMenuPanel;

public class AccountingMenuPanel extends VerticalMenuPanel {

    public AccountingMenuPanel(MessageService messages, MenuButtonFactory buttonFactory, Runnable addMovement,
            Runnable listMovements, Runnable exportMovements, Runnable back) {
        super(messages.get("menu.accounting.title"),
                createButtons(messages, buttonFactory, addMovement, listMovements, exportMovements, back), 60);
    }

    private static JButton[] createButtons(MessageService messages, MenuButtonFactory buttonFactory, Runnable addMovement,
            Runnable listMovements, Runnable exportMovements, Runnable back) {
        return new JButton[] {
            buttonFactory.create(messages.get("menu.accounting.add"), addMovement),
            buttonFactory.create(messages.get("menu.accounting.list"), listMovements),
            buttonFactory.create(messages.get("menu.accounting.export"), exportMovements),
            buttonFactory.create(messages.get("menu.accounting.back"), back)
        };
    }
}
