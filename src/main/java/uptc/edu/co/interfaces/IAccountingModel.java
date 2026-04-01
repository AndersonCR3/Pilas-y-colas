package uptc.edu.co.interfaces;

import java.math.BigDecimal;

import uptc.edu.co.pojo.AccountingMovement;
import uptc.edu.co.structures.DoubleList;

public interface IAccountingModel {
    AccountingMovement createMovement(String description, String movementType, BigDecimal value, String dateTime);

    DoubleList<AccountingMovement> getMovementsLifo();
}
