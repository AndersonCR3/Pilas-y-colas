package uptc.edu.co.view.panels;

import javax.swing.JButton;

import uptc.edu.co.i18n.MessageService;
import uptc.edu.co.view.components.MenuButtonFactory;
import uptc.edu.co.view.components.VerticalMenuPanel;

public class PersonMenuPanel extends VerticalMenuPanel {

    public PersonMenuPanel(MessageService messages, MenuButtonFactory buttonFactory, Runnable addPerson,
            Runnable removePerson, Runnable removeLast, Runnable listPeople, Runnable exportPeople, Runnable back) {
        super(messages.get("menu.person.title"),
                createButtons(messages, buttonFactory, addPerson, removePerson, removeLast, listPeople, exportPeople, back), 60);
    }

    private static JButton[] createButtons(MessageService messages, MenuButtonFactory buttonFactory, Runnable addPerson,
            Runnable removePerson, Runnable removeLast, Runnable listPeople, Runnable exportPeople, Runnable back) {
        return new JButton[] {
            buttonFactory.create(messages.get("menu.person.add"), addPerson),
            buttonFactory.create(messages.get("menu.person.remove"), removePerson),
            buttonFactory.create(messages.get("menu.person.removeLast"), removeLast),
            buttonFactory.create(messages.get("menu.person.list"), listPeople),
            buttonFactory.create(messages.get("menu.person.export"), exportPeople),
            buttonFactory.create(messages.get("menu.person.back"), back)
        };
    }
}
