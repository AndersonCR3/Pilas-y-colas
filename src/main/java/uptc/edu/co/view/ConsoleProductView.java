package uptc.edu.co.view;

import java.util.Scanner;

import uptc.edu.co.i18n.MessageService;

public class ConsoleProductView implements ProductView {
    private final Scanner scanner;
    private final MessageService messages;

    public ConsoleProductView(Scanner scanner, MessageService messages) {
        this.scanner = scanner;
        this.messages = messages;
    }

    public void showProductMenu() {
        System.out.println("\n----- " + messages.get("menu.product.title") + " -----");
        System.out.println("1. " + messages.get("menu.product.add"));
        System.out.println("2. " + messages.get("menu.product.remove"));
        System.out.println("3. " + messages.get("menu.product.list"));
        System.out.println("4. " + messages.get("menu.product.export"));
        System.out.println("5. " + messages.get("menu.product.back"));
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

    public String readText(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public void showMessage(String message) {
        System.out.println(message);
    }
}
