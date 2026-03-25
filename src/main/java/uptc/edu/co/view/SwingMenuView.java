package uptc.edu.co.view;

import uptc.edu.co.i18n.MessageService;

public class SwingMenuView extends AbstractSwingDialogView implements MenuView {

    public SwingMenuView(MessageService messages) {
        super(messages);
    }

    public void showMainMenu() {
        String title = getMessages().get("menu.main.title");
        String text = "1. " + getMessages().get("menu.main.persons")
                + "\n2. " + getMessages().get("menu.main.products")
                + "\n3. " + getMessages().get("menu.main.accounting")
                + "\n4. " + getMessages().get("menu.main.exit")
                + "\n\n" + getMessages().get("menu.option.prompt");
        setCurrentMenu(title, text, 4);
    }

    public int readOption() {
        return readOptionFromDialog();
    }

    public void showMessage(String message) {
        showMessageInDialog(message);
    }
}
