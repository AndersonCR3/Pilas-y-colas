package uptc.edu.co.view;

import uptc.edu.co.i18n.MessageService;

public class SwingProductView extends AbstractSwingDialogView implements ProductView {

    public SwingProductView(MessageService messages) {
        super(messages);
    }

    public void showProductMenu() {
        String title = getMessages().get("menu.product.title");
        String text = "1. " + getMessages().get("menu.product.add")
                + "\n2. " + getMessages().get("menu.product.remove")
                + "\n3. " + getMessages().get("menu.product.list")
                + "\n4. " + getMessages().get("menu.product.export")
                + "\n5. " + getMessages().get("menu.product.back")
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
