package uptc.edu.co.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import uptc.edu.co.i18n.MessageService;
import uptc.edu.co.mediator.GuiMenuMediator;
import uptc.edu.co.pojo.AccountingMovement;
import uptc.edu.co.pojo.Person;
import uptc.edu.co.pojo.Product;
import uptc.edu.co.presenter.ActionResult;
import uptc.edu.co.view.components.MenuButtonFactory;
import uptc.edu.co.view.panels.AccountingMenuPanel;
import uptc.edu.co.view.panels.MainMenuPanel;
import uptc.edu.co.view.panels.PersonMenuPanel;
import uptc.edu.co.view.panels.ProductMenuPanel;

public class MainFrame extends JFrame {
    private static final DateTimeFormatter BIRTH_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final MessageService messages;
    private final GuiMenuMediator menuMediator;
    private final MenuButtonFactory buttonFactory;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    public MainFrame(MessageService messages, GuiMenuMediator menuMediator) {
        this.messages = messages;
        this.menuMediator = menuMediator;
        this.buttonFactory = new MenuButtonFactory();
        this.cardLayout = new CardLayout();
        this.cardPanel = new JPanel(cardLayout);
        initializeFrame();
        initializeCards();
    }

    private void initializeFrame() {
        setTitle(messages.get("menu.main.title"));
        setSize(980, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(cardPanel, BorderLayout.CENTER);
    }

    private void initializeCards() {
        cardPanel.add(createMainMenuPanel(), "MAIN");
        cardPanel.add(createPersonMenuPanel(), "PERSON");
        cardPanel.add(createProductMenuPanel(), "PRODUCT");
        cardPanel.add(createAccountingMenuPanel(), "ACCOUNTING");
    }

    private JPanel createMainMenuPanel() {
        return new MainMenuPanel(messages, buttonFactory, this::openPersonCard, this::openProductCard,
                this::openAccountingCard, this::dispose);
    }

    private JPanel createPersonMenuPanel() {
        return new PersonMenuPanel(messages, buttonFactory, this::addPerson, this::removePersonByParameter,
                this::removeLastPerson, this::openPersonListWindow, this::exportPeopleToCsv, this::openMainCard);
    }

    private JPanel createProductMenuPanel() {
        return new ProductMenuPanel(messages, buttonFactory, this::addProduct, this::removeProductByParameter,
                this::openProductListWindow, this::exportProductsToCsv, this::openMainCard);
    }

    private JPanel createAccountingMenuPanel() {
        return new AccountingMenuPanel(messages, buttonFactory, this::addAccountingMovement,
                this::openAccountingListWindow, this::exportAccountingToCsv, this::openMainCard);
    }

    private void addPerson() {
        PersonFormData data = readPersonFormData();
        if (data == null) {
            return;
        }
        ActionResult result = menuMediator.registerPerson(data.names, data.lastNames, data.gender, data.birthDate);
        showMessage(result.getMessage());
    }

    private PersonFormData readPersonFormData() {
        JTextField namesField = new JTextField();
        JTextField lastNamesField = new JTextField();
        JComboBox<String> genderCombo = new JComboBox<String>(new String[] { "Masculino", "Femenino" });
        JTextField birthDateField = new JTextField();
        JPanel form = buildPersonForm(namesField, lastNamesField, genderCombo, birthDateField);
        if (!confirmForm(form, "menu.person.add")) {
            return null;
        }
        return new PersonFormData(safeTrim(namesField.getText()), safeTrim(lastNamesField.getText()),
                String.valueOf(genderCombo.getSelectedItem()), safeTrim(birthDateField.getText()));
    }

    private JPanel buildPersonForm(JTextField namesField, JTextField lastNamesField, JComboBox<String> genderCombo,
            JTextField birthDateField) {
        JPanel form = createFormPanel();
        addFormRow(form, "person.prompt.names", namesField);
        addFormRow(form, "person.prompt.lastNames", lastNamesField);
        addFormRow(form, "person.prompt.gender", genderCombo);
        addFormRow(form, "person.prompt.birthDate", birthDateField);
        return form;
    }

    private void removePersonByParameter() {
        String param = readInput("person.remove.prompt.parameter");
        if (isBlank(param)) {
            return;
        }
        ActionResult result = menuMediator.removePersonByParameter(param.trim());
        showMessage(result.getMessage());
    }

    private void removeLastPerson() {
        ActionResult result = menuMediator.removeLastPerson();
        showMessage(result.getMessage());
    }

    private void openPersonListWindow() {
        PersonListContext context = createPersonListContext();
        configurePersonPager(context);
        refreshPersonList(context);
        showListWindow(context.frame, context.table, context.bottomPanel);
    }

    private PersonListContext createPersonListContext() {
        JFrame frame = createListFrame("menu.person.list");
        DefaultTableModel model = createPersonTableModel();
        JTable table = createAlignedTable(model);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel pageLabel = new JLabel("1/1");
        int[] pageIndex = new int[] { 0 };
        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");
        bottomPanel.add(prevButton);
        bottomPanel.add(pageLabel);
        bottomPanel.add(nextButton);
        return new PersonListContext(frame, table, model, bottomPanel, pageLabel, pageIndex, prevButton, nextButton);
    }

    private DefaultTableModel createPersonTableModel() {
        Object[] columns = new Object[] { messages.get("person.list.col.names"), messages.get("person.list.col.lastNames"),
            messages.get("person.list.col.gender"), messages.get("person.list.col.age") };
        return nonEditableModel(columns);
    }

    private void configurePersonPager(PersonListContext context) {
        context.prevButton.addActionListener(e -> goPreviousPersonPage(context));
        context.nextButton.addActionListener(e -> goNextPersonPage(context));
    }

    private void goPreviousPersonPage(PersonListContext context) {
        if (context.pageIndex[0] <= 0) {
            return;
        }
        context.pageIndex[0]--;
        refreshPersonList(context);
    }

    private void goNextPersonPage(PersonListContext context) {
        if (context.pageIndex[0] >= totalPersonPages() - 1) {
            return;
        }
        context.pageIndex[0]++;
        refreshPersonList(context);
    }

    private void refreshPersonList(PersonListContext context) {
        context.model.setRowCount(0);
        List<Person> people = menuMediator.getPeople();
        int totalPages = adjustPersonPageIndex(context, people);
        addPersonRows(context.model, people, context.pageIndex[0], menuMediator.getPersonPageSize());
        context.pageLabel.setText((context.pageIndex[0] + 1) + "/" + totalPages);
    }

    private int adjustPersonPageIndex(PersonListContext context, List<Person> people) {
        int totalPages = calculatePages(people.size(), menuMediator.getPersonPageSize());
        if (context.pageIndex[0] >= totalPages) {
            context.pageIndex[0] = Math.max(0, totalPages - 1);
        }
        return totalPages;
    }

    private void addPersonRows(DefaultTableModel model, List<Person> people, int pageIndex, int pageSize) {
        int start = pageIndex * pageSize;
        int end = Math.min(start + pageSize, people.size());
        for (int index = start; index < end; index++) {
            Person person = people.get(index);
            model.addRow(new Object[] { person.getNames(), person.getLastNames(), person.getGender(),
                Integer.valueOf(calculateAge(person.getBirthDate())) });
        }
    }

    private int totalPersonPages() {
        return calculatePages(menuMediator.getPeople().size(), menuMediator.getPersonPageSize());
    }

    private int calculatePages(int size, int pageSize) {
        return size == 0 ? 1 : (size + pageSize - 1) / pageSize;
    }

    private void exportPeopleToCsv() {
        List<Person> people = menuMediator.getPeople();
        if (people.isEmpty()) {
            showMessage(messages.get("person.export.empty"));
            return;
        }
        File file = chooseExportFile("personas.csv");
        if (file != null) {
            showMessage(menuMediator.exportPeople(file).getMessage());
        }
    }

    private void addProduct() {
        ProductFormData data = readProductFormData();
        if (data == null || data.price == null || data.quantity == null) {
            return;
        }
        ActionResult result = menuMediator.registerProduct(data.description, data.unit, data.quantity, data.price);
        showMessage(result.getMessage());
    }

    private ProductFormData readProductFormData() {
        JTextField descriptionField = new JTextField();
        JComboBox<String> unitCombo = new JComboBox<String>(new String[] { "libra", "kilos", "bultos", "toneladas" });
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();
        JPanel form = buildProductForm(descriptionField, unitCombo, quantityField, priceField);
        if (!confirmForm(form, "menu.product.add")) {
            return null;
        }
        BigDecimal quantity = parseDecimal(quantityField.getText(), "product.error.quantity");
        BigDecimal price = parseDecimal(priceField.getText(), "product.error.price");
        return new ProductFormData(safeTrim(descriptionField.getText()), String.valueOf(unitCombo.getSelectedItem()), quantity, price);
    }

    private JPanel buildProductForm(JTextField descriptionField, JComboBox<String> unitCombo, JTextField quantityField,
            JTextField priceField) {
        JPanel form = createFormPanel();
        addFormRow(form, "product.prompt.description", descriptionField);
        addFormRow(form, "product.prompt.unit", unitCombo);
        addFormRow(form, "product.prompt.quantity", quantityField);
        addFormRow(form, "product.prompt.price", priceField);
        return form;
    }

    private BigDecimal parseDecimal(String raw, String errorKey) {
        try {
            return new BigDecimal(safeTrim(raw));
        } catch (Exception exception) {
            showMessage(messages.get(errorKey));
            return null;
        }
    }

    private void removeProductByParameter() {
        String param = readInput("product.remove.prompt.parameter");
        if (isBlank(param)) {
            return;
        }
        ActionResult result = menuMediator.removeProductByParameter(param.trim());
        showMessage(result.getMessage());
    }

    private void openProductListWindow() {
        JFrame frame = createListFrame("menu.product.list");
        DefaultTableModel model = nonEditableModel(new Object[] { "ID", "DESCRIPCION", "UNIDAD", "CANTIDAD", "PRECIO" });
        addProductRows(model, menuMediator.getProducts());
        JTable table = createAlignedTable(model);
        showListWindow(frame, table, null);
    }

    private void addProductRows(DefaultTableModel model, List<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            model.addRow(new Object[] { Integer.valueOf(product.getId()), product.getDescription(), product.getUnit(),
                product.getQuantity().toPlainString(), product.getPrice().toPlainString() });
        }
    }

    private void exportProductsToCsv() {
        List<Product> products = menuMediator.getProducts();
        if (products.isEmpty()) {
            showMessage(messages.get("product.export.empty"));
            return;
        }
        File file = chooseExportFile("productos.csv");
        if (file != null) {
            showMessage(menuMediator.exportProducts(file).getMessage());
        }
    }

    private void addAccountingMovement() {
        AccountingFormData data = readAccountingFormData();
        if (data == null || data.value == null) {
            return;
        }
        if (!isValidDateTime(data.dateTime)) {
            showMessage(messages.get("accounting.error.datetime"));
            return;
        }
        createAccountingMovement(data);
    }

    private void createAccountingMovement(AccountingFormData data) {
        ActionResult result = menuMediator.registerMovement(data.description, data.type, data.value, data.dateTime);
        showMessage(result.getMessage());
    }

    private AccountingFormData readAccountingFormData() {
        JTextField descriptionField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<String>(new String[] { "Ingreso", "Egreso" });
        JTextField valueField = new JTextField();
        JTextField dateTimeField = new JTextField();
        JPanel form = buildAccountingForm(descriptionField, typeCombo, valueField, dateTimeField);
        if (!confirmForm(form, "menu.accounting.add")) {
            return null;
        }
        BigDecimal value = parseDecimal(valueField.getText(), "accounting.error.value");
        return new AccountingFormData(safeTrim(descriptionField.getText()), String.valueOf(typeCombo.getSelectedItem()),
                value, safeTrim(dateTimeField.getText()));
    }

    private JPanel buildAccountingForm(JTextField descriptionField, JComboBox<String> typeCombo, JTextField valueField,
            JTextField dateTimeField) {
        JPanel form = createFormPanel();
        addFormRow(form, "accounting.prompt.description", descriptionField);
        addFormRow(form, "accounting.prompt.type", typeCombo);
        addFormRow(form, "accounting.prompt.value", valueField);
        addFormRow(form, "accounting.prompt.datetime", dateTimeField);
        return form;
    }

    private void openAccountingListWindow() {
        JFrame frame = createListFrame("menu.accounting.list");
        DefaultTableModel model = nonEditableModel(new Object[] { "ID", "DESCRIPCION", "TIPO", "VALOR", "FECHA HORA" });
        List<AccountingMovement> movements = menuMediator.getMovementsLifo();
        BigDecimal total = addAccountingRows(model, movements);
        JTable table = createAlignedTable(model);
        JLabel totalLabel = new JLabel(messages.get("accounting.list.total") + " " + total.toPlainString());
        showListWindow(frame, table, totalLabel);
    }

    private BigDecimal addAccountingRows(DefaultTableModel model, List<AccountingMovement> movements) {
        for (int i = 0; i < movements.size(); i++) {
            AccountingMovement movement = movements.get(i);
            model.addRow(new Object[] { Integer.valueOf(movement.getId()), movement.getDescription(), movement.getMovementType(),
                movement.getValue().toPlainString(), movement.getDateTime() });
        }
        return menuMediator.calculateAccountingTotal(movements);
    }

    private void exportAccountingToCsv() {
        List<AccountingMovement> movements = menuMediator.getMovementsLifo();
        if (movements.isEmpty()) {
            showMessage(messages.get("accounting.export.empty"));
            return;
        }
        File file = chooseExportFile("contabilidad.csv");
        if (file != null) {
            showMessage(menuMediator.exportAccounting(file).getMessage());
        }
    }

    private void showListWindow(JFrame frame, JTable table, java.awt.Component south) {
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        if (south != null) {
            frame.add(south, BorderLayout.SOUTH);
        }
        frame.setVisible(true);
    }

    private JFrame createListFrame(String titleKey) {
        JFrame frame = new JFrame(messages.get(titleKey));
        frame.setSize(760, 420);
        frame.setLocationRelativeTo(this);
        return frame;
    }

    private JTable createAlignedTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        setTableLeftAlignment(table);
        return table;
    }

    private DefaultTableModel nonEditableModel(Object[] columns) {
        return new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JPanel createFormPanel() {
        return new JPanel(new GridLayout(0, 2, 8, 8));
    }

    private void addFormRow(JPanel form, String key, java.awt.Component field) {
        form.add(new JLabel(messages.get(key)));
        form.add(field);
    }

    private boolean confirmForm(JPanel form, String titleKey) {
        int result = JOptionPane.showConfirmDialog(this, form, messages.get(titleKey), JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        return result == JOptionPane.OK_OPTION;
    }

    private String readInput(String key) {
        return JOptionPane.showInputDialog(this, messages.get(key));
    }

    private void setTableLeftAlignment(JTable table) {
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        for (int index = 0; index < table.getColumnCount(); index++) {
            table.getColumnModel().getColumn(index).setCellRenderer(leftRenderer);
        }
    }

    private void openMainCard() {
        showCard("MAIN");
    }

    private void openPersonCard() {
        showCard("PERSON");
    }

    private void openProductCard() {
        showCard("PRODUCT");
    }

    private void openAccountingCard() {
        showCard("ACCOUNTING");
    }

    private void showCard(String cardName) {
        cardLayout.show(cardPanel, cardName);
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private File chooseExportFile(String defaultFileName) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(defaultFileName));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return chooser.getSelectedFile();
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private String safeTrim(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isValidLength(String value, int minLength, int maxLength) {
        return value != null && value.length() >= minLength && value.length() <= maxLength;
    }

    private boolean isValidBirthDate(String date) {
        return isValidDate(date, "dd/MM/yyyy");
    }

    private boolean isValidDateTime(String dateTime) {
        return isValidDate(dateTime, "dd/MM/yyyy HH:mm");
    }

    private boolean isValidDate(String raw, String pattern) {
        if (isBlank(raw)) {
            return false;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setLenient(false);
        try {
            formatter.parse(raw.trim());
            return true;
        } catch (ParseException exception) {
            return false;
        }
    }

    private int calculateAge(String birthDate) {
        try {
            LocalDate birth = LocalDate.parse(birthDate, BIRTH_DATE_FORMATTER);
            return Period.between(birth, LocalDate.now()).getYears();
        } catch (DateTimeParseException exception) {
            return 0;
        }
    }

    private void closeQuietly(BufferedWriter writer) {
        if (writer == null) {
            return;
        }
        try {
            writer.close();
        } catch (IOException ignored) {
            showMessage("No se pudo cerrar archivo.");
        }
    }

    private static final class PersonFormData {
        private final String names;
        private final String lastNames;
        private final String gender;
        private final String birthDate;

        private PersonFormData(String names, String lastNames, String gender, String birthDate) {
            this.names = names;
            this.lastNames = lastNames;
            this.gender = gender;
            this.birthDate = birthDate;
        }
    }

    private static final class ProductFormData {
        private final String description;
        private final String unit;
        private final BigDecimal quantity;
        private final BigDecimal price;

        private ProductFormData(String description, String unit, BigDecimal quantity, BigDecimal price) {
            this.description = description;
            this.unit = unit;
            this.quantity = quantity;
            this.price = price;
        }
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

    private static final class PersonListContext {
        private final JFrame frame;
        private final JTable table;
        private final DefaultTableModel model;
        private final JPanel bottomPanel;
        private final JLabel pageLabel;
        private final int[] pageIndex;
        private final JButton prevButton;
        private final JButton nextButton;

        private PersonListContext(JFrame frame, JTable table, DefaultTableModel model, JPanel bottomPanel, JLabel pageLabel,
                int[] pageIndex, JButton prevButton, JButton nextButton) {
            this.frame = frame;
            this.table = table;
            this.model = model;
            this.bottomPanel = bottomPanel;
            this.pageLabel = pageLabel;
            this.pageIndex = pageIndex;
            this.prevButton = prevButton;
            this.nextButton = nextButton;
        }
    }
}
