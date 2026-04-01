package uptc.edu.co.bootstrap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uptc.edu.co.config.AppConfig;
import uptc.edu.co.interfaces.IAccountingModel;
import uptc.edu.co.interfaces.IPersonModel;
import uptc.edu.co.interfaces.IProductModel;
import uptc.edu.co.i18n.MessageService;
import uptc.edu.co.mediator.GuiMenuMediator;
import uptc.edu.co.model.AccountingModel;
import uptc.edu.co.model.PersonModel;
import uptc.edu.co.model.ProductModel;
import uptc.edu.co.presenter.AccountingPresenter;
import uptc.edu.co.presenter.PersonPresenter;
import uptc.edu.co.presenter.ProductPresenter;
import uptc.edu.co.view.MainFrame;

public class ApplicationBootstrap {
    private static final Logger LOGGER = LogManager.getLogger(ApplicationBootstrap.class);

    public void start() {
        AppConfig appConfig = new AppConfig();
        MessageService messages = new MessageService(appConfig);

        IPersonModel personModel = new PersonModel(appConfig);
        IProductModel productModel = new ProductModel(appConfig);
        IAccountingModel accountingModel = new AccountingModel(appConfig);

        PersonPresenter personPresenter = new PersonPresenter(personModel, messages);
        ProductPresenter productPresenter = new ProductPresenter(productModel, messages);
        AccountingPresenter accountingPresenter = new AccountingPresenter(accountingModel, messages);
        GuiMenuMediator guiMenuMediator = new GuiMenuMediator(personPresenter, productPresenter, accountingPresenter);

        LOGGER.info("Aplicacion iniciada en modo GUI Swing. Directorio de configuracion externa: {}",
                appConfig.getExternalConfigDir().getAbsolutePath());

        MainFrame frame = new MainFrame(messages, guiMenuMediator);
        frame.setVisible(true);
    }
}
