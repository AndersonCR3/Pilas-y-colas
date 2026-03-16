package uptc.edu.co.mediator;

import uptc.edu.co.presenter.PersonPresenter;
import uptc.edu.co.view.MenuView;
import uptc.edu.co.view.PersonView;

public class ConsoleMenuMediator implements MenuMediator {
    private final MenuView menuView;
    private final PersonView personView;
    private final PersonPresenter personPresenter;

    public ConsoleMenuMediator(MenuView menuView, PersonView personView, PersonPresenter personPresenter) {
        this.menuView = menuView;
        this.personView = personView;
        this.personPresenter = personPresenter;
    }

    public void start() {
        boolean running = true;
        while (running) {
            menuView.showMainMenu();
            int option = menuView.readOption();

            switch (option) {
                case 1:
                    handlePersonSubMenu();
                    break;
                case 2:
                    menuView.showMessage("Modulo Productos en construccion.");
                    break;
                case 3:
                    menuView.showMessage("Modulo Contabilidad en construccion.");
                    break;
                case 4:
                    menuView.showMessage("Hasta luego.");
                    running = false;
                    break;
                default:
                    menuView.showMessage("Opcion no valida.");
                    break;
            }
        }
    }

    private void handlePersonSubMenu() {
        boolean inPersonMenu = true;
        while (inPersonMenu) {
            personView.showPersonMenu();
            int option = personView.readOption();

            switch (option) {
                case 1:
                    personPresenter.registerPerson();
                    break;
                case 2:
                    personPresenter.listPeople();
                    break;
                case 3:
                    personPresenter.exportPeopleToCsv();
                    break;
                case 4:
                    inPersonMenu = false;
                    break;
                default:
                    personView.showMessage("Opcion no valida.");
                    break;
            }
        }
    }
}
