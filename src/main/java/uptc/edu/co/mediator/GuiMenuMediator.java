package uptc.edu.co.mediator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import uptc.edu.co.i18n.MessageService;
import uptc.edu.co.interfaces.IAccountingModel;
import uptc.edu.co.interfaces.IPersonModel;
import uptc.edu.co.interfaces.IProductModel;
import uptc.edu.co.pojo.AccountingMovement;
import uptc.edu.co.pojo.Person;
import uptc.edu.co.pojo.Product;
import uptc.edu.co.presenter.ActionResult;
import uptc.edu.co.structures.DoubleList;

public class GuiMenuMediator {
    private final IPersonModel personModel;
    private final IProductModel productModel;
    private final IAccountingModel accountingModel;
    private final MessageService messages;

    public GuiMenuMediator(IPersonModel personModel, IProductModel productModel, IAccountingModel accountingModel,
            MessageService messages) {
        this.personModel = personModel;
        this.productModel = productModel;
        this.accountingModel = accountingModel;
        this.messages = messages;
    }

    public ActionResult registerPerson(String names, String lastNames, String gender, String birthDate) {
        ActionResult lengthValidation = validatePersonLengths(names, lastNames);
        if (lengthValidation != null) {
            return lengthValidation;
        }
        if (!isValidBirthDate(birthDate)) {
            return ActionResult.failure(messages.get("person.error.birthDate"));
        }
        Person person = personModel.createPerson(safeTrim(names), safeTrim(lastNames), safeTrim(gender),
                safeTrim(birthDate));
        return ActionResult.success(messages.get("person.success.created") + " " + person.getId());
    }

    private ActionResult validatePersonLengths(String names, String lastNames) {
        if (!isValidLength(names, personModel.getMinNamesLength(), personModel.getMaxNamesLength())) {
            return ActionResult.failure(messages.get("person.error.namesLength") + " " + personModel.getMinNamesLength()
                    + " - " + personModel.getMaxNamesLength());
        }
        if (!isValidLength(lastNames, personModel.getMinLastNamesLength(), personModel.getMaxLastNamesLength())) {
            return ActionResult.failure(messages.get("person.error.lastNamesLength") + " "
                    + personModel.getMinLastNamesLength() + " - " + personModel.getMaxLastNamesLength());
        }
        return null;
    }

    public ActionResult removePersonByParameter(String param) {
        Person removed;
        try {
            removed = personModel.removePersonById(Integer.parseInt(safeTrim(param)));
        } catch (NumberFormatException exception) {
            removed = personModel.removePersonByName(safeTrim(param));
        }
        if (removed == null) {
            return ActionResult.failure(messages.get("person.remove.notFound"));
        }
        return ActionResult.success(messages.get("person.remove.success") + " " + removed.getNames() + " "
                + removed.getLastNames());
    }

    public ActionResult removeLastPerson() {
        Person removed = personModel.removeLastPerson();
        if (removed == null) {
            return ActionResult.failure(messages.get("person.remove.empty"));
        }
        return ActionResult.success(messages.get("person.removeLast.success") + " " + removed.getNames() + " "
                + removed.getLastNames());
    }

    public DoubleList<Person> getPeople() {
        return personModel.getPeople();
    }

    public int getPersonPageSize() {
        return personModel.getPageSize();
    }

    public ActionResult exportPeople(File file) {
        DoubleList<Person> people = personModel.getPeople();
        return exportToCsv(file, people.size(), "person.export.empty", "person.export.success", "person.export.error",
                "id,nombres,apellidos,genero,fecha_nacimiento", new CsvRowWriter() {
                    public void writeRow(BufferedWriter writer, int index) throws IOException {
                        Person person = people.get(index);
                        writer.write(person.getId() + "," + escapeCsv(person.getNames()) + ","
                                + escapeCsv(person.getLastNames()) + "," + escapeCsv(person.getGender()) + ","
                                + escapeCsv(person.getBirthDate()));
                    }
                });
    }

    public ActionResult registerProduct(String description, String unit, BigDecimal quantity, BigDecimal price) {
        try {
            Product product = productModel.createProduct(description, unit, quantity, price);
            return ActionResult.success(messages.get("product.success.created") + " " + product.getId());
        } catch (IllegalArgumentException exception) {
            return ActionResult.failure(messages.get("product.error.validation") + " " + exception.getMessage());
        }
    }

    public ActionResult removeProductByParameter(String param) {
        Product removed;
        try {
            removed = productModel.removeProductById(Integer.parseInt(safeTrim(param)));
        } catch (NumberFormatException exception) {
            removed = productModel.removeProductByName(safeTrim(param));
        }
        if (removed == null) {
            return ActionResult.failure(messages.get("product.remove.notFound"));
        }
        return ActionResult.success(messages.get("product.remove.success") + " id=" + removed.getId() + " | "
                + removed.getDescription());
    }

    public DoubleList<Product> getProducts() {
        return productModel.getProducts();
    }

    public ActionResult exportProducts(File file) {
        DoubleList<Product> products = productModel.getProducts();
        return exportToCsv(file, products.size(), "product.export.empty", "product.export.success",
                "product.export.error", "id,descripcion,unidad,cantidad,precio", new CsvRowWriter() {
                    public void writeRow(BufferedWriter writer, int index) throws IOException {
                        Product product = products.get(index);
                        writer.write(product.getId() + "," + escapeCsv(product.getDescription()) + ","
                                + escapeCsv(product.getUnit()) + "," + product.getQuantity().toPlainString() + ","
                                + product.getPrice().toPlainString());
                    }
                });
    }

    public ActionResult registerMovement(String description, String type, BigDecimal value, String dateTime) {
        if (!isValidDateTime(dateTime)) {
            return ActionResult.failure(messages.get("accounting.error.datetime"));
        }
        try {
            accountingModel.createMovement(description, type, value, dateTime);
            return ActionResult.success(messages.get("accounting.success.created"));
        } catch (IllegalArgumentException exception) {
            return ActionResult.failure(messages.get("accounting.error.validation") + " " + exception.getMessage());
        }
    }

    public DoubleList<AccountingMovement> getMovementsLifo() {
        return accountingModel.getMovementsLifo();
    }

    public BigDecimal calculateAccountingTotal(DoubleList<AccountingMovement> movements) {
        BigDecimal total = BigDecimal.ZERO;
        for (int index = 0; index < movements.size(); index++) {
            AccountingMovement movement = movements.get(index);
            if ("Ingreso".equalsIgnoreCase(movement.getMovementType())) {
                total = total.add(movement.getValue());
            } else {
                total = total.subtract(movement.getValue());
            }
        }
        return total;
    }

    public ActionResult exportAccounting(File file) {
        DoubleList<AccountingMovement> movements = accountingModel.getMovementsLifo();
        return exportToCsv(file, movements.size(), "accounting.export.empty", "accounting.export.success",
                "accounting.export.error", "id,descripcion,tipo,valor,fecha_hora", new CsvRowWriter() {
                    public void writeRow(BufferedWriter writer, int index) throws IOException {
                        AccountingMovement movement = movements.get(index);
                        writer.write(movement.getId() + "," + escapeCsv(movement.getDescription()) + ","
                                + escapeCsv(movement.getMovementType()) + "," + movement.getValue().toPlainString()
                                + "," + escapeCsv(movement.getDateTime()));
                    }
                });
    }

    private ActionResult exportToCsv(File file, int size, String emptyKey, String successKey, String errorKey,
            String header, CsvRowWriter rowWriter) {
        if (size == 0) {
            return ActionResult.failure(messages.get(emptyKey));
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(header);
            writer.newLine();
            for (int index = 0; index < size; index++) {
                rowWriter.writeRow(writer, index);
                writer.newLine();
            }
            return ActionResult.success(messages.get(successKey) + " " + file.getAbsolutePath());
        } catch (IOException exception) {
            return ActionResult.failure(messages.get(errorKey) + " " + exception.getMessage());
        } finally {
            closeQuietly(writer);
        }
    }

    private interface CsvRowWriter {
        void writeRow(BufferedWriter writer, int index) throws IOException;
    }

    private boolean isValidLength(String value, int minLength, int maxLength) {
        return value != null && value.length() >= minLength && value.length() <= maxLength;
    }

    private boolean isValidBirthDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            return false;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        formatter.setLenient(false);
        try {
            formatter.parse(date);
            return true;
        } catch (ParseException exception) {
            return false;
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

    private String safeTrim(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
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
