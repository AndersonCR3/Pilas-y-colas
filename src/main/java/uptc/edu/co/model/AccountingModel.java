package uptc.edu.co.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import uptc.edu.co.interfaces.IAccountingModel;
import uptc.edu.co.pojo.AccountingMovement;

public class AccountingModel implements IAccountingModel {
    private final Stack<AccountingMovement> movements;
    private int nextId;

    public AccountingModel() {
        this.movements = new Stack<AccountingMovement>();
        this.nextId = 1;
    }

    public AccountingMovement createMovement(String description, String movementType, BigDecimal value, String dateTime) {
        validateDescription(description);
        validateType(movementType);
        validateValue(value);

        AccountingMovement movement = new AccountingMovement(nextId, description.trim(), movementType.trim(), value,
                dateTime.trim());
        nextId++;
        movements.push(movement);
        return movement;
    }

    public List<AccountingMovement> getMovementsLifo() {
        List<AccountingMovement> snapshot = new ArrayList<AccountingMovement>(movements);
        Collections.reverse(snapshot);
        return Collections.unmodifiableList(snapshot);
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
