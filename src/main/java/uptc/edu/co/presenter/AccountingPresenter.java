package uptc.edu.co.presenter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import uptc.edu.co.i18n.MessageService;
import uptc.edu.co.interfaces.IAccountingModel;
import uptc.edu.co.pojo.AccountingMovement;

public class AccountingPresenter {
    private final IAccountingModel accountingModel;
    private final MessageService messages;

    public AccountingPresenter(IAccountingModel accountingModel, MessageService messages) {
        this.accountingModel = accountingModel;
        this.messages = messages;
    }

    public ActionResult registerMovement(String description, String movementType, BigDecimal value, String dateTime) {
        if (!isValidDateTime(dateTime)) {
            return ActionResult.failure(messages.get("accounting.error.datetime"));
        }
        try {
            accountingModel.createMovement(description, movementType, value, dateTime);
            return ActionResult.success(messages.get("accounting.success.created"));
        } catch (IllegalArgumentException exception) {
            return ActionResult.failure(messages.get("accounting.error.validation") + " " + exception.getMessage());
        }
    }

    public List<AccountingMovement> getMovementsLifo() {
        return accountingModel.getMovementsLifo();
    }

    public BigDecimal calculateTotal(List<AccountingMovement> movements) {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < movements.size(); i++) {
            AccountingMovement movement = movements.get(i);
            total = "Ingreso".equalsIgnoreCase(movement.getMovementType()) ? total.add(movement.getValue())
                    : total.subtract(movement.getValue());
        }
        return total;
    }

    public ActionResult exportMovements(File file) {
        List<AccountingMovement> movements = accountingModel.getMovementsLifo();
        if (movements.isEmpty()) {
            return ActionResult.failure(messages.get("accounting.export.empty"));
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("id,descripcion,tipo,valor,fecha_hora");
            writer.newLine();
            for (int i = 0; i < movements.size(); i++) {
                AccountingMovement movement = movements.get(i);
                writer.write(movement.getId() + "," + escapeCsv(movement.getDescription()) + ","
                        + escapeCsv(movement.getMovementType()) + "," + movement.getValue().toPlainString() + ","
                        + escapeCsv(movement.getDateTime()));
                writer.newLine();
            }
            return ActionResult.success(messages.get("accounting.export.success") + " " + file.getAbsolutePath());
        } catch (IOException exception) {
            return ActionResult.failure(messages.get("accounting.export.error") + " " + exception.getMessage());
        } finally {
            closeQuietly(writer);
        }
    }

    private boolean isValidDateTime(String dateTime) {
        if (dateTime == null || dateTime.trim().isEmpty()) {
            return false;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        formatter.setLenient(false);
        try {
            formatter.parse(dateTime.trim());
            return true;
        } catch (ParseException exception) {
            return false;
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private void closeQuietly(BufferedWriter writer) {
        if (writer == null) {
            return;
        }
        try {
            writer.close();
        } catch (IOException ignored) {
        }
    }
}
