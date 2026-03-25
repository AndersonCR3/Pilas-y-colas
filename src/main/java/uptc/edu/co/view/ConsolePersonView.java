package uptc.edu.co.view;

import java.util.Scanner;

import uptc.edu.co.i18n.MessageService;

public class ConsolePersonView implements PersonView {
    private final Scanner scanner;
    private final MessageService messages;

    public ConsolePersonView(Scanner scanner, MessageService messages) {
        this.scanner = scanner;
        this.messages = messages;
    }

    public void showPersonMenu() {
        System.out.println("\n----- " + messages.get("menu.person.title") + " -----");
        System.out.println("1. " + messages.get("menu.person.add"));
        System.out.println("2. " + messages.get("menu.person.remove"));
        System.out.println("3. " + messages.get("menu.person.list"));
        System.out.println("4. " + messages.get("menu.person.export"));
        System.out.println("5. " + messages.get("menu.person.back"));
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
