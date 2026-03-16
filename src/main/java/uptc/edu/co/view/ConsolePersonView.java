package uptc.edu.co.view;

import java.util.Scanner;

public class ConsolePersonView implements PersonView {
    private final Scanner scanner;

    public ConsolePersonView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showPersonMenu() {
        System.out.println("\n----- SUBMENU PERSONAS -----");
        System.out.println("1. Adicionar");
        System.out.println("2. Listar");
        System.out.println("3. Exportar a archivo CSV");
        System.out.println("4. Regresar al menu anterior");
        System.out.print("Seleccione una opcion: ");
    }

    public int readOption() {
        while (!scanner.hasNextInt()) {
            scanner.nextLine();
            System.out.print("Ingrese una opcion valida: ");
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
