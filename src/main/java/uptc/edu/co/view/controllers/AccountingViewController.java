package uptc.edu.co.view.controllers;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import uptc.edu.co.i18n.MessageService;
import uptc.edu.co.pojo.AccountingMovement;
import uptc.edu.co.presenter.ActionResult;
import uptc.edu.co.presenter.AccountingPresenter;
import uptc.edu.co.structures.DoubleList;

public class AccountingViewController extends AbstractViewController {
    private final AccountingPresenter presenter;

    public AccountingViewController(JFrame parent, MessageService messages, AccountingPresenter presenter) {
        super(parent, messages);
        this.presenter = presenter;
    }

    public void addAccountingMovement() {
        AccountingFormData data = readAccountingFormData();
        if (data == null || data.value == null) {
            return;
        }
        if (!isValidDateTime(data.dateTime)) {
            showMessage(messages.get("accounting.error.datetime"));
            return;
        }
        ActionResult result = presenter.registerMovement(data.description, data.type, data.value, data.dateTime);
        showMessage(result.getMessage());
    }

    public void openAccountingListWindow() {
        JFrame frame = createListFrame("menu.accounting.list");
        DefaultTableModel model = nonEditableModel(new Object[] { messages.get("accounting.list.col.id"),
            messages.get("accounting.list.col.description"), messages.get("accounting.list.col.type"),
            messages.get("accounting.list.col.value"), messages.get("accounting.list.col.datetime") });
        DoubleList<AccountingMovement> movements = presenter.getMovementsLifo();
        BigDecimal total = addAccountingRows(model, movements);
        JTable table = createAlignedTable(model);
        JLabel totalLabel = new JLabel(messages.get("accounting.list.total") + " " + total.toPlainString());
        showListWindow(frame, table, totalLabel);
    }

    public void exportAccountingToCsv() {
        DoubleList<AccountingMovement> movements = presenter.getMovementsLifo();
        if (movements.isEmpty()) {
            showMessage(messages.get("accounting.export.empty"));
            return;
        }
        File file = chooseExportFile("contabilidad.csv");
        if (file != null) {
            showMessage(presenter.exportAccounting(file).getMessage());
        }
    }

    private AccountingFormData readAccountingFormData() {
        JTextField descriptionField = new JTextField();
        JComboBox<String> typeCombo = createMovementTypeCombo();
        JTextField valueField = new JTextField();
        JTextField dateTimeField = new JTextField();
        javax.swing.JPanel form = createFormPanel();
        buildAccountingForm(form, descriptionField, typeCombo, valueField, dateTimeField);
        if (!confirmForm(form, "menu.accounting.add")) {
            return null;
        }
        return buildAccountingData(descriptionField, typeCombo, valueField, dateTimeField);
    }

    private JComboBox<String> createMovementTypeCombo() {
        return new JComboBox<String>(
                new String[] { messages.get("option.movement.income"), messages.get("option.movement.expense") });
    }

    private void buildAccountingForm(javax.swing.JPanel form, JTextField descriptionField, JComboBox<String> typeCombo,
            JTextField valueField, JTextField dateTimeField) {
        addFormRow(form, "accounting.prompt.description", descriptionField);
        addFormRow(form, "accounting.prompt.type", typeCombo);
        addFormRow(form, "accounting.prompt.value", valueField);
        addFormRow(form, "accounting.prompt.datetime", dateTimeField);
    }

    private AccountingFormData buildAccountingData(JTextField descriptionField, JComboBox<String> typeCombo,
            JTextField valueField, JTextField dateTimeField) {
        BigDecimal value = parseDecimal(valueField.getText(), "accounting.error.value");
        String canonicalType = toCanonicalMovementType(String.valueOf(typeCombo.getSelectedItem()));
        return new AccountingFormData(safeTrim(descriptionField.getText()), canonicalType, value,
                safeTrim(dateTimeField.getText()));
    }

    private BigDecimal parseDecimal(String raw, String errorKey) {
        try {
            return new BigDecimal(safeTrim(raw));
        } catch (Exception exception) {
            showMessage(messages.get(errorKey));
            return null;
        }
    }

    private boolean isValidDateTime(String dateTime) {
        if (isBlank(dateTime)) {
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

    private BigDecimal addAccountingRows(DefaultTableModel model, DoubleList<AccountingMovement> movements) {
        for (int index = 0; index < movements.size(); index++) {
            AccountingMovement movement = movements.get(index);
            model.addRow(new Object[] { Integer.valueOf(movement.getId()), movement.getDescription(),
                    localizeMovementType(movement.getMovementType()), movement.getValue().toPlainString(),
                    movement.getDateTime() });
        }
        return presenter.calculateAccountingTotal(movements);
    }

    private String toCanonicalMovementType(String selectedLabel) {
        if (messages.get("option.movement.income").equals(selectedLabel)) {
            return "Ingreso";
        }
        if (messages.get("option.movement.expense").equals(selectedLabel)) {
            return "Egreso";
        }
        return safeTrim(selectedLabel);
    }

    private String localizeMovementType(String canonicalType) {
        if ("ingreso".equalsIgnoreCase(canonicalType)) {
            return messages.get("option.movement.income");
        }
        if ("egreso".equalsIgnoreCase(canonicalType)) {
            return messages.get("option.movement.expense");
        }
        return canonicalType;
    }

    private static final class AccountingFormData {
        private final String description;
        private final String type;
        private final BigDecimal value;
        private final String dateTime;

        private AccountingFormData(String description, String type, BigDecimal value, String dateTime) {
            this.description = description;
            this.type = type;
            this.value = value;
            this.dateTime = dateTime;
        }
    }
}
