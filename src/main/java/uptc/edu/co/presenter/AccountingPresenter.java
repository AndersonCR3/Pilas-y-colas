package uptc.edu.co.presenter;

import java.io.File;
import java.math.BigDecimal;

import uptc.edu.co.mediator.GuiMenuMediator;
import uptc.edu.co.pojo.AccountingMovement;
import uptc.edu.co.structures.DoubleList;

public class AccountingPresenter {
    private final GuiMenuMediator mediator;

    public AccountingPresenter(GuiMenuMediator mediator) {
        this.mediator = mediator;
    }

    public ActionResult registerMovement(String description, String type, BigDecimal value, String dateTime) {
        return mediator.registerMovement(description, type, value, dateTime);
    }

    public DoubleList<AccountingMovement> getMovementsLifo() {
        return mediator.getMovementsLifo();
    }

    public BigDecimal calculateAccountingTotal(DoubleList<AccountingMovement> movements) {
        return mediator.calculateAccountingTotal(movements);
    }

    public ActionResult exportAccounting(File file) {
        return mediator.exportAccounting(file);
    }
}
