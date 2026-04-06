package uptc.edu.co.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import uptc.edu.co.i18n.MessageService;
import uptc.edu.co.interfaces.IAccountingModel;
import uptc.edu.co.interfaces.IPersonModel;
import uptc.edu.co.interfaces.IProductModel;
import uptc.edu.co.mediator.GuiMenuMediator;
import uptc.edu.co.presenter.AccountingPresenter;
import uptc.edu.co.presenter.PersonPresenter;
import uptc.edu.co.presenter.ProductPresenter;
import uptc.edu.co.view.components.MenuButtonFactory;
import uptc.edu.co.view.controllers.AccountingViewController;
import uptc.edu.co.view.controllers.PersonViewController;
import uptc.edu.co.view.controllers.ProductViewController;
import uptc.edu.co.view.panels.AccountingMenuPanel;
import uptc.edu.co.view.panels.MainMenuPanel;
import uptc.edu.co.view.panels.PersonMenuPanel;
import uptc.edu.co.view.panels.ProductMenuPanel;

public class MainFrame extends JFrame {
    private final MessageService messages;
    private final MenuButtonFactory buttonFactory;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final PersonViewController personController;
    private final ProductViewController productController;
    private final AccountingViewController accountingController;

    public MainFrame(MessageService messages, IPersonModel personModel, IProductModel productModel,
            IAccountingModel accountingModel) {
        this.messages = messages;
        this.buttonFactory = new MenuButtonFactory();
        this.cardLayout = new CardLayout();
        this.cardPanel = new JPanel(cardLayout);

        GuiMenuMediator menuMediator = new GuiMenuMediator(personModel, productModel, accountingModel, messages);
        PersonPresenter personPresenter = new PersonPresenter(menuMediator);
        ProductPresenter productPresenter = new ProductPresenter(menuMediator);
        AccountingPresenter accountingPresenter = new AccountingPresenter(menuMediator);

        this.personController = new PersonViewController(this, messages, personPresenter);
        this.productController = new ProductViewController(this, messages, productPresenter);
        this.accountingController = new AccountingViewController(this, messages, accountingPresenter);

        initializeFrame();
        initializeCards();
    }

    private void initializeFrame() {
        setTitle(messages.get("menu.main.title"));
        setSize(980, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(cardPanel, BorderLayout.CENTER);
    }

    private void initializeCards() {
        addMainCard();
        addPersonCard();
        addProductCard();
        addAccountingCard();
        }

        private void addMainCard() {
        cardPanel.add(new MainMenuPanel(messages, buttonFactory, this::openPersonCard, this::openProductCard,
            this::openAccountingCard, this::dispose), "MAIN");
        }

        private void addPersonCard() {
        cardPanel.add(new PersonMenuPanel(messages, buttonFactory, personController::addPerson,
            personController::removePersonByParameter, personController::removeLastPerson,
            personController::openPersonListWindow, personController::exportPeopleToCsv, this::openMainCard),
            "PERSON");
        }

        private void addProductCard() {
        cardPanel.add(new ProductMenuPanel(messages, buttonFactory, productController::addProduct,
            productController::removeProductByParameter, productController::openProductListWindow,
            productController::exportProductsToCsv, this::openMainCard), "PRODUCT");
        }

        private void addAccountingCard() {
        cardPanel.add(new AccountingMenuPanel(messages, buttonFactory, accountingController::addAccountingMovement,
            accountingController::openAccountingListWindow, accountingController::exportAccountingToCsv,
            this::openMainCard), "ACCOUNTING");
    }

    private void openMainCard() {
        cardLayout.show(cardPanel, "MAIN");
    }

    private void openPersonCard() {
        cardLayout.show(cardPanel, "PERSON");
    }

    private void openProductCard() {
        cardLayout.show(cardPanel, "PRODUCT");
    }

    private void openAccountingCard() {
        cardLayout.show(cardPanel, "ACCOUNTING");
    }
}
