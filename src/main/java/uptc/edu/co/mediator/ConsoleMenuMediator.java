package uptc.edu.co.mediator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uptc.edu.co.i18n.MessageService;
import uptc.edu.co.presenter.AccountingPresenter;
import uptc.edu.co.presenter.PersonPresenter;
import uptc.edu.co.presenter.ProductPresenter;
import uptc.edu.co.view.AccountingView;
import uptc.edu.co.view.MenuView;
import uptc.edu.co.view.PersonView;
import uptc.edu.co.view.ProductView;

public class ConsoleMenuMediator implements MenuMediator {
    private static final Logger LOGGER = LogManager.getLogger(ConsoleMenuMediator.class);

    private final MenuView menuView;
    private final PersonView personView;
    private final ProductView productView;
    private final AccountingView accountingView;
    private final PersonPresenter personPresenter;
    private final ProductPresenter productPresenter;
    private final AccountingPresenter accountingPresenter;
    private final MessageService messages;

    public ConsoleMenuMediator(MenuView menuView, PersonView personView, ProductView productView,
            AccountingView accountingView, PersonPresenter personPresenter, ProductPresenter productPresenter,
            AccountingPresenter accountingPresenter, MessageService messages) {
        this.menuView = menuView;
        this.personView = personView;
        this.productView = productView;
        this.accountingView = accountingView;
        this.personPresenter = personPresenter;
        this.productPresenter = productPresenter;
        this.accountingPresenter = accountingPresenter;
        this.messages = messages;
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
                    handleProductSubMenu();
                    break;
                case 3:
                    handleAccountingSubMenu();
                    break;
                case 4:
                    menuView.showMessage(messages.get("app.goodbye"));
                    LOGGER.info("Aplicacion finalizada por usuario.");
                    running = false;
                    break;
                default:
                    menuView.showMessage(messages.get("menu.option.invalid"));
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
                    personPresenter.removePersonFromQueue();
                    break;
                case 3:
                    personPresenter.listPeople();
                    break;
                case 4:
                    personPresenter.exportPeopleToCsv();
                    break;
                case 5:
                    inPersonMenu = false;
                    break;
                default:
                    personView.showMessage(messages.get("menu.option.invalid"));
                    break;
            }
        }
    }

    private void handleProductSubMenu() {
        boolean inProductMenu = true;
        while (inProductMenu) {
            productView.showProductMenu();
            int option = productView.readOption();

            switch (option) {
                case 1:
                    productPresenter.registerProduct();
                    break;
                case 2:
                    productPresenter.removeProductFromList();
                    break;
                case 3:
                    productPresenter.listProducts();
                    break;
                case 4:
                    productPresenter.exportProductsToCsv();
                    break;
                case 5:
                    inProductMenu = false;
                    break;
                default:
                    productView.showMessage(messages.get("menu.option.invalid"));
                    break;
            }
        }
    }

    private void handleAccountingSubMenu() {
        boolean inAccountingMenu = true;
        while (inAccountingMenu) {
            accountingView.showAccountingMenu();
            int option = accountingView.readOption();

            switch (option) {
                case 1:
                    accountingPresenter.registerMovement();
                    break;
                case 2:
                    accountingPresenter.listMovements();
                    break;
                case 3:
                    accountingPresenter.exportMovementsToCsv();
                    break;
                case 4:
                    inAccountingMenu = false;
                    break;
                default:
                    accountingView.showMessage(messages.get("menu.option.invalid"));
                    break;
            }
        }
    }
}
