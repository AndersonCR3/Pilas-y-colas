package uptc.edu.co.pojo;

import java.math.BigDecimal;

public class AccountingMovement {
    private final int id;
    private final String description;
    private final String movementType;
    private final BigDecimal value;
    private final String dateTime;

    public AccountingMovement(int id, String description, String movementType, BigDecimal value, String dateTime) {
        this.id = id;
        this.description = description;
        this.movementType = movementType;
        this.value = value;
        this.dateTime = dateTime;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getMovementType() {
        return movementType;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getDateTime() {
        return dateTime;
    }
}
