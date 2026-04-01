package uptc.edu.co.model;

import java.math.BigDecimal;

import uptc.edu.co.config.AppConfig;
import uptc.edu.co.interfaces.IAccountingModel;
import uptc.edu.co.pojo.AccountingMovement;
import uptc.edu.co.structures.CollectionMode;
import uptc.edu.co.structures.DoubleList;
import uptc.edu.co.structures.ManagerCollection;

public class AccountingModel implements IAccountingModel {
    private final ManagerCollection<AccountingMovement> movements;
    private int nextId;

    public AccountingModel(AppConfig appConfig) {
        CollectionMode mode = CollectionMode.from(appConfig.getString("accounting.collection.mode", "STACK"),
                CollectionMode.STACK);
        this.movements = new ManagerCollection<AccountingMovement>(mode);
        this.nextId = 1;
    }

    public AccountingMovement createMovement(String description, String movementType, BigDecimal value, String dateTime) {
        validateDescription(description);
        validateType(movementType);
        validateValue(value);

        AccountingMovement movement = new AccountingMovement(nextId, description.trim(), movementType.trim(), value,
                dateTime.trim());
        nextId++;
        movements.add(movement);
        return movement;
    }

    public DoubleList<AccountingMovement> getMovementsLifo() {
        return movements.getOrdered();
    }

    private void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Descripcion vacia");
        }
    }

    private void validateType(String movementType) {
        String type = movementType == null ? "" : movementType.trim().toLowerCase();
        if (!"ingreso".equals(type) && !"egreso".equals(type)) {
            throw new IllegalArgumentException("Tipo de movimiento invalido");
        }
    }

    private void validateValue(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor invalido");
        }
    }
}
