package uptc.edu.co.view;

import uptc.edu.co.i18n.MessageService;

public class SwingPersonView extends AbstractSwingDialogView implements PersonView {

    public SwingPersonView(MessageService messages) {
        super(messages);
    }

    public void showPersonMenu() {
        String title = getMessages().get("menu.person.title");
        String text = "1. " + getMessages().get("menu.person.add")
                + "\n2. " + getMessages().get("menu.person.remove")
                + "\n3. " + getMessages().get("menu.person.list")
                + "\n4. " + getMessages().get("menu.person.export")
                + "\n5. " + getMessages().get("menu.person.back")
                + "\n\n" + getMessages().get("menu.option.prompt");
        setCurrentMenu(title, text, 5);
    }

    public int readOption() {
        return readOptionFromDialog();
    }

    public String readText(String prompt) {
        return readTextFromDialog(prompt);
    }

    public void showMessage(String message) {
        showMessageInDialog(message);
    }
}
