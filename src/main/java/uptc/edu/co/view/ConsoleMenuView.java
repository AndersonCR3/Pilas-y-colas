package uptc.edu.co.view;

import java.util.Scanner;

import uptc.edu.co.i18n.MessageService;

public class ConsoleMenuView implements MenuView {
    private final Scanner scanner;
    private final MessageService messages;

    public ConsoleMenuView(Scanner scanner, MessageService messages) {
        this.scanner = scanner;
        this.messages = messages;
    }

    public void showMainMenu() {
        System.out.println("\n===== " + messages.get("menu.main.title") + " =====");
        System.out.println("1. " + messages.get("menu.main.persons"));
        System.out.println("2. " + messages.get("menu.main.products"));
        System.out.println("3. " + messages.get("menu.main.accounting"));
        System.out.println("4. " + messages.get("menu.main.exit"));
        System.out.print(messages.get("menu.option.prompt"));
    }

    public int readOption() {
        while (!scanner.hasNextInt()) {
            scanner.nextLine();
            System.out.print(messages.get("menu.option.invalidPrompt"));
        }
        int option = scanner.nextInt();
        scanner.nextLine();
        return option;
    }

    public void showMessage(String message) {
        System.out.println(message);
    }
}
