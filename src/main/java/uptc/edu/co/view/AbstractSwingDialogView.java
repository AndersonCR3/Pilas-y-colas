package uptc.edu.co.view;

import javax.swing.JOptionPane;

import uptc.edu.co.i18n.MessageService;

public abstract class AbstractSwingDialogView {
    private final MessageService messages;
    private String currentTitle;
    private String currentMenuText;
    private int cancelOption;

    protected AbstractSwingDialogView(MessageService messages) {
        this.messages = messages;
        this.currentTitle = "Menu";
        this.currentMenuText = "";
        this.cancelOption = 0;
    }

    protected MessageService getMessages() {
        return messages;
    }

    protected void setCurrentMenu(String title, String menuText, int cancelOption) {
        this.currentTitle = title;
        this.currentMenuText = menuText;
        this.cancelOption = cancelOption;
    }

    protected int readOptionFromDialog() {
        while (true) {
            String input = JOptionPane.showInputDialog(null, currentMenuText, currentTitle, JOptionPane.QUESTION_MESSAGE);
            if (input == null) {
                return cancelOption;
            }

            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(null, messages.get("menu.option.invalid"), currentTitle,
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    protected String readTextFromDialog(String prompt) {
        String value = JOptionPane.showInputDialog(null, prompt, currentTitle, JOptionPane.QUESTION_MESSAGE);
        if (value == null) {
            return "";
        }
        return value;
    }

    protected void showMessageInDialog(String message) {
        JOptionPane.showMessageDialog(null, message, currentTitle, JOptionPane.INFORMATION_MESSAGE);
    }
}
