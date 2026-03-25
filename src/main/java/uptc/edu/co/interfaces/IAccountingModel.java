package uptc.edu.co.interfaces;

import java.math.BigDecimal;
import java.util.List;

import uptc.edu.co.pojo.AccountingMovement;

public interface IAccountingModel {
    AccountingMovement createMovement(String description, String movementType, BigDecimal value, String dateTime);

    List<AccountingMovement> getMovementsLifo();
}
