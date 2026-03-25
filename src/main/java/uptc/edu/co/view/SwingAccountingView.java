package uptc.edu.co.view;

import uptc.edu.co.i18n.MessageService;

public class SwingAccountingView extends AbstractSwingDialogView implements AccountingView {

    public SwingAccountingView(MessageService messages) {
        super(messages);
    }

    public void showAccountingMenu() {
        String title = getMessages().get("menu.accounting.title");
        String text = "1. " + getMessages().get("menu.accounting.add")
                + "\n2. " + getMessages().get("menu.accounting.list")
                + "\n3. " + getMessages().get("menu.accounting.export")
                + "\n4. " + getMessages().get("menu.accounting.back")
                + "\n\n" + getMessages().get("menu.option.prompt");
        setCurrentMenu(title, text, 4);
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
