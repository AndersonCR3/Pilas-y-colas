package uptc.edu.co.presenter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uptc.edu.co.i18n.MessageService;
import uptc.edu.co.interfaces.IAccountingModel;
import uptc.edu.co.pojo.AccountingMovement;
import uptc.edu.co.view.AccountingView;

public class AccountingPresenter {
    private static final Logger LOGGER = LogManager.getLogger(AccountingPresenter.class);

    private final IAccountingModel accountingModel;
    private final AccountingView accountingView;
    private final MessageService messages;

    public AccountingPresenter(IAccountingModel accountingModel, AccountingView accountingView, MessageService messages) {
        this.accountingModel = accountingModel;
        this.accountingView = accountingView;
        this.messages = messages;
    }

    public void registerMovement() {
        String description = accountingView.readText(messages.get("accounting.prompt.description"));
        String movementType = accountingView.readText(messages.get("accounting.prompt.type"));
        String valueText = accountingView.readText(messages.get("accounting.prompt.value"));
        String dateTime = accountingView.readText(messages.get("accounting.prompt.datetime"));

        BigDecimal value;
        try {
            value = new BigDecimal(valueText.trim());
        } catch (Exception exception) {
            accountingView.showMessage(messages.get("accounting.error.value"));
            return;
        }

        if (!isValidDateTime(dateTime)) {
            accountingView.showMessage(messages.get("accounting.error.datetime"));
            return;
        }

        try {
            AccountingMovement movement = accountingModel.createMovement(description, normalizeType(movementType), value,
                    dateTime.trim());
            accountingView.showMessage(messages.get("accounting.success.created") + " " + movement.getId());
            LOGGER.info("Movimiento contable registrado con id {}", Integer.valueOf(movement.getId()));
        } catch (IllegalArgumentException exception) {
            accountingView.showMessage(messages.get("accounting.error.validation") + " " + exception.getMessage());
        }
    }

    public void listMovements() {
        List<AccountingMovement> movements = accountingModel.getMovementsLifo();
        if (movements.isEmpty()) {
            accountingView.showMessage(messages.get("accounting.list.empty"));
            return;
        }

        accountingView.showMessage(messages.get("accounting.list.header"));
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < movements.size(); i++) {
            AccountingMovement movement = movements.get(i);
            accountingView.showMessage((i + 1) + ". id=" + movement.getId() + " | descripcion=" + movement.getDescription()
                    + " | tipo=" + movement.getMovementType() + " | valor=" + movement.getValue().toPlainString()
                    + " | fechaHora=" + movement.getDateTime());

            if ("Ingreso".equalsIgnoreCase(movement.getMovementType())) {
                total = total.add(movement.getValue());
            } else {
                total = total.subtract(movement.getValue());
            }
        }

        accountingView.showMessage(messages.get("accounting.list.total") + " " + total.toPlainString());
    }

    public void exportMovementsToCsv() {
        List<AccountingMovement> movements = accountingModel.getMovementsLifo();
        if (movements.isEmpty()) {
            accountingView.showMessage(messages.get("accounting.export.empty"));
            return;
        }

        String fileName = accountingView.readText(messages.get("accounting.export.fileName"));
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "contabilidad.csv";
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileName.trim()));
            writer.write("id,descripcion,tipo,valor,fecha_hora");
            writer.newLine();

            for (int i = 0; i < movements.size(); i++) {
                AccountingMovement movement = movements.get(i);
                writer.write(movement.getId() + "," + escapeCsv(movement.getDescription()) + ","
                        + escapeCsv(movement.getMovementType()) + "," + movement.getValue().toPlainString() + ","
                        + escapeCsv(movement.getDateTime()));
                writer.newLine();
            }

            accountingView.showMessage(messages.get("accounting.export.success") + " " + fileName.trim());
            LOGGER.info("Movimientos exportados a CSV: {}", fileName.trim());
        } catch (IOException exception) {
            accountingView.showMessage(messages.get("accounting.export.error") + " " + exception.getMessage());
            LOGGER.error("Error exportando contabilidad a CSV", exception);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                    LOGGER.warn("No se pudo cerrar el archivo CSV de contabilidad.");
                }
            }
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

    private String normalizeType(String movementType) {
        if (movementType == null) {
            return "";
        }
        String type = movementType.trim().toLowerCase();
        if ("ingreso".equals(type)) {
            return "Ingreso";
        }
        if ("egreso".equals(type)) {
            return "Egreso";
        }
        return movementType.trim();
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
