package uptc.edu.co;

import java.util.Scanner;

import uptc.edu.co.mediator.ConsoleMenuMediator;
import uptc.edu.co.mediator.MenuMediator;
import uptc.edu.co.model.PersonModel;
import uptc.edu.co.presenter.PersonPresenter;
import uptc.edu.co.view.ConsoleMenuView;
import uptc.edu.co.view.ConsolePersonView;
import uptc.edu.co.view.MenuView;
import uptc.edu.co.view.PersonView;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        PersonModel personModel = new PersonModel();

        MenuView menuView = new ConsoleMenuView(scanner);
        PersonView personView = new ConsolePersonView(scanner);
        PersonPresenter personPresenter = new PersonPresenter(personModel, personView);

        MenuMediator menuMediator = new ConsoleMenuMediator(menuView, personView, personPresenter);
        menuMediator.start();

        scanner.close();
    }
}
