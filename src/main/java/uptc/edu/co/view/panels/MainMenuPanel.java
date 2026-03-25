package uptc.edu.co.view.panels;

import javax.swing.JButton;

import uptc.edu.co.i18n.MessageService;
import uptc.edu.co.view.components.MenuButtonFactory;
import uptc.edu.co.view.components.VerticalMenuPanel;

public class MainMenuPanel extends VerticalMenuPanel {

    public MainMenuPanel(MessageService messages, MenuButtonFactory buttonFactory, Runnable openPerson,
            Runnable openProduct, Runnable openAccounting, Runnable exitApp) {
        super(messages.get("menu.main.title"), createButtons(messages, buttonFactory, openPerson, openProduct, openAccounting, exitApp), 80);
    }

    private static JButton[] createButtons(MessageService messages, MenuButtonFactory buttonFactory, Runnable openPerson,
            Runnable openProduct, Runnable openAccounting, Runnable exitApp) {
        return new JButton[] {
            buttonFactory.create(messages.get("menu.main.persons"), openPerson),
            buttonFactory.create(messages.get("menu.main.products"), openProduct),
            buttonFactory.create(messages.get("menu.main.accounting"), openAccounting),
            buttonFactory.create(messages.get("menu.main.exit"), exitApp)
        };
    }
}
