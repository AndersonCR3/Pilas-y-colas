package uptc.edu.co.view;

import java.util.Scanner;

public class ConsoleMenuView implements MenuView {
    private final Scanner scanner;

    public ConsoleMenuView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showMainMenu() {
        System.out.println("\n===== MENU PRINCIPAL =====");
        System.out.println("1. Personas");
        System.out.println("2. Productos");
        System.out.println("3. Contabilidad");
        System.out.println("4. Salir/terminar");
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

    public void showMessage(String message) {
        System.out.println(message);
    }
}
