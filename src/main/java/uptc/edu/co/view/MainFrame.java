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
import uptc.edu.co.interfaces.IAccountingModel;
import uptc.edu.co.interfaces.IPersonModel;
import uptc.edu.co.interfaces.IProductModel;
import uptc.edu.co.pojo.AccountingMovement;
import uptc.edu.co.pojo.Person;
import uptc.edu.co.pojo.Product;

public class MainFrame extends JFrame {
    private static final DateTimeFormatter BIRTH_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final MessageService messages;
    private final IPersonModel personModel;
    private final IProductModel productModel;
    private final IAccountingModel accountingModel;

    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    private DefaultTableModel personTableModel;
    private JLabel personPageLabel;
    private int personPageIndex;

    private DefaultTableModel productTableModel;

    private DefaultTableModel accountingTableModel;
    private JLabel accountingTotalLabel;

    public MainFrame(MessageService messages, IPersonModel personModel, IProductModel productModel,
            IAccountingModel accountingModel) {
        this.messages = messages;
        this.personModel = personModel;
        this.productModel = productModel;
        this.accountingModel = accountingModel;

        this.cardLayout = new CardLayout();
        this.cardPanel = new JPanel(cardLayout);
        this.personPageIndex = 0;

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
        cardPanel.add(createPersonPanel(), "PERSON");
        cardPanel.add(createProductPanel(), "PRODUCT");
        cardPanel.add(createAccountingPanel(), "ACCOUNTING");
    }

    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel title = new JLabel(messages.get("menu.main.title"), SwingConstants.CENTER);
        panel.add(title, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel(new GridLayout(4, 1, 12, 12));

        JButton personsButton = new JButton(messages.get("menu.main.persons"));
        personsButton.addActionListener(e -> showCard("PERSON"));

        JButton productsButton = new JButton(messages.get("menu.main.products"));
        productsButton.addActionListener(e -> showCard("PRODUCT"));

        JButton accountingButton = new JButton(messages.get("menu.main.accounting"));
        accountingButton.addActionListener(e -> showCard("ACCOUNTING"));

        JButton exitButton = new JButton(messages.get("menu.main.exit"));
        exitButton.addActionListener(e -> dispose());

        buttonsPanel.add(personsButton);
        buttonsPanel.add(productsButton);
        buttonsPanel.add(accountingButton);
        buttonsPanel.add(exitButton);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 80));
        centerPanel.add(buttonsPanel);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPersonPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton(messages.get("menu.person.add"));
        JButton removeButton = new JButton(messages.get("menu.person.remove"));
        JButton listButton = new JButton(messages.get("menu.person.list"));
        JButton exportButton = new JButton(messages.get("menu.person.export"));
        JButton backButton = new JButton(messages.get("menu.person.back"));

        addButton.addActionListener(e -> addPerson());
        removeButton.addActionListener(e -> removePerson());
        listButton.addActionListener(e -> refreshPersonTable());
        exportButton.addActionListener(e -> exportPeopleToCsv());
        backButton.addActionListener(e -> showCard("MAIN"));

        topButtons.add(addButton);
        topButtons.add(removeButton);
        topButtons.add(listButton);
        topButtons.add(exportButton);
        topButtons.add(backButton);

        personTableModel = new DefaultTableModel(new Object[] {
            messages.get("person.list.col.names"),
            messages.get("person.list.col.lastNames"),
            messages.get("person.list.col.gender"),
            messages.get("person.list.col.age")
        }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(personTableModel);
        setTableLeftAlignment(table);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");
        personPageLabel = new JLabel("1/1");

        prevButton.addActionListener(e -> {
            if (personPageIndex > 0) {
                personPageIndex--;
                refreshPersonTable();
            }
        });

        nextButton.addActionListener(e -> {
            int totalPages = calculateTotalPersonPages();
            if (personPageIndex < totalPages - 1) {
                personPageIndex++;
                refreshPersonTable();
            }
        });

        bottomPanel.add(prevButton);
        bottomPanel.add(personPageLabel);
        bottomPanel.add(nextButton);

        panel.add(topButtons, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        refreshPersonTable();
        return panel;
    }

    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton(messages.get("menu.product.add"));
        JButton removeButton = new JButton(messages.get("menu.product.remove"));
        JButton listButton = new JButton(messages.get("menu.product.list"));
        JButton exportButton = new JButton(messages.get("menu.product.export"));
        JButton backButton = new JButton(messages.get("menu.product.back"));

        addButton.addActionListener(e -> addProduct());
        removeButton.addActionListener(e -> removeProduct());
        listButton.addActionListener(e -> refreshProductTable());
        exportButton.addActionListener(e -> exportProductsToCsv());
        backButton.addActionListener(e -> showCard("MAIN"));

        topButtons.add(addButton);
        topButtons.add(removeButton);
        topButtons.add(listButton);
        topButtons.add(exportButton);
        topButtons.add(backButton);

        productTableModel = new DefaultTableModel(new Object[] { "ID", "DESCRIPCION", "UNIDAD", "PRECIO" }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(productTableModel);
        setTableLeftAlignment(table);

        panel.add(topButtons, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refreshProductTable();
        return panel;
    }

    private JPanel createAccountingPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton(messages.get("menu.accounting.add"));
        JButton listButton = new JButton(messages.get("menu.accounting.list"));
        JButton exportButton = new JButton(messages.get("menu.accounting.export"));
        JButton backButton = new JButton(messages.get("menu.accounting.back"));

        addButton.addActionListener(e -> addAccountingMovement());
        listButton.addActionListener(e -> refreshAccountingTable());
        exportButton.addActionListener(e -> exportAccountingToCsv());
        backButton.addActionListener(e -> showCard("MAIN"));

        topButtons.add(addButton);
        topButtons.add(listButton);
        topButtons.add(exportButton);
        topButtons.add(backButton);

        accountingTableModel = new DefaultTableModel(new Object[] { "ID", "DESCRIPCION", "TIPO", "VALOR", "FECHA HORA" },
                0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(accountingTableModel);
        setTableLeftAlignment(table);

        accountingTotalLabel = new JLabel(messages.get("accounting.list.total") + " 0");

        panel.add(topButtons, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(accountingTotalLabel, BorderLayout.SOUTH);

        refreshAccountingTable();
        return panel;
    }

    private void addPerson() {
        JTextField namesField = new JTextField();
        JTextField lastNamesField = new JTextField();
        JComboBox<String> genderCombo = new JComboBox<String>(new String[] { "Masculino", "Femenino" });
        JTextField birthDateField = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel(messages.get("person.prompt.names")));
        form.add(namesField);
        form.add(new JLabel(messages.get("person.prompt.lastNames")));
        form.add(lastNamesField);
        form.add(new JLabel(messages.get("person.prompt.gender")));
        form.add(genderCombo);
        form.add(new JLabel(messages.get("person.prompt.birthDate")));
        form.add(birthDateField);

        int result = JOptionPane.showConfirmDialog(this, form, messages.get("menu.person.add"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String names = safeTrim(namesField.getText());
        String lastNames = safeTrim(lastNamesField.getText());
        String gender = String.valueOf(genderCombo.getSelectedItem());
        String birthDate = safeTrim(birthDateField.getText());

        if (!isValidLength(names, personModel.getMinNamesLength(), personModel.getMaxNamesLength())) {
            showMessage(messages.get("person.error.namesLength") + " " + personModel.getMinNamesLength() + " - "
                    + personModel.getMaxNamesLength());
            return;
        }

        if (!isValidLength(lastNames, personModel.getMinLastNamesLength(), personModel.getMaxLastNamesLength())) {
            showMessage(messages.get("person.error.lastNamesLength") + " " + personModel.getMinLastNamesLength() + " - "
                    + personModel.getMaxLastNamesLength());
            return;
        }

        if (!isValidBirthDate(birthDate)) {
            showMessage(messages.get("person.error.birthDate"));
            return;
        }

        Person person = personModel.createPerson(names, lastNames, gender, birthDate);
        showMessage(messages.get("person.success.created") + " " + person.getId());
        refreshPersonTable();
    }

    private void removePerson() {
        Person removed = personModel.removeNextPerson();
        if (removed == null) {
            showMessage(messages.get("person.remove.empty"));
            return;
        }

        showMessage(messages.get("person.remove.success") + " " + removed.getNames() + " " + removed.getLastNames());
        int totalPages = calculateTotalPersonPages();
        if (personPageIndex >= totalPages) {
            personPageIndex = Math.max(0, totalPages - 1);
        }
        refreshPersonTable();
    }

    private void refreshPersonTable() {
        personTableModel.setRowCount(0);
        List<Person> people = personModel.getPeople();
        int pageSize = personModel.getPageSize();
        int totalPages = calculateTotalPersonPages();
        if (personPageIndex >= totalPages) {
            personPageIndex = Math.max(0, totalPages - 1);
        }

        int start = personPageIndex * pageSize;
        int end = Math.min(start + pageSize, people.size());

        for (int index = start; index < end; index++) {
            Person person = people.get(index);
            personTableModel.addRow(new Object[] {
                person.getNames(),
                person.getLastNames(),
                person.getGender(),
                Integer.valueOf(calculateAge(person.getBirthDate()))
            });
        }

        personPageLabel.setText((totalPages == 0 ? 0 : personPageIndex + 1) + "/" + totalPages);
    }

    private int calculateTotalPersonPages() {
        int size = personModel.getPeople().size();
        int pageSize = personModel.getPageSize();
        if (size == 0) {
            return 1;
        }
        return (size + pageSize - 1) / pageSize;
    }

    private void exportPeopleToCsv() {
        List<Person> people = personModel.getPeople();
        if (people.isEmpty()) {
            showMessage(messages.get("person.export.empty"));
            return;
        }

        File file = chooseExportFile("personas.csv");
        if (file == null) {
            return;
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("id,nombres,apellidos,genero,fecha_nacimiento");
            writer.newLine();

            for (int i = 0; i < people.size(); i++) {
                Person person = people.get(i);
                writer.write(person.getId() + "," + escapeCsv(person.getNames()) + "," + escapeCsv(person.getLastNames())
                        + "," + escapeCsv(person.getGender()) + "," + escapeCsv(person.getBirthDate()));
                writer.newLine();
            }
            showMessage(messages.get("person.export.success") + " " + file.getAbsolutePath());
        } catch (IOException exception) {
            showMessage(messages.get("person.export.error") + " " + exception.getMessage());
        } finally {
            closeQuietly(writer);
        }
    }

    private void addProduct() {
        JTextField descriptionField = new JTextField();
        JComboBox<String> unitCombo = new JComboBox<String>(new String[] { "libra", "kilos", "bultos", "toneladas" });
        JTextField priceField = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel(messages.get("product.prompt.description")));
        form.add(descriptionField);
        form.add(new JLabel(messages.get("product.prompt.unit")));
        form.add(unitCombo);
        form.add(new JLabel(messages.get("product.prompt.price")));
        form.add(priceField);

        int result = JOptionPane.showConfirmDialog(this, form, messages.get("menu.product.add"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(safeTrim(priceField.getText()));
        } catch (Exception exception) {
            showMessage(messages.get("product.error.price"));
            return;
        }

        try {
            Product product = productModel.createProduct(safeTrim(descriptionField.getText()),
                    String.valueOf(unitCombo.getSelectedItem()), price);
            showMessage(messages.get("product.success.created") + " " + product.getId());
            refreshProductTable();
        } catch (IllegalArgumentException exception) {
            showMessage(messages.get("product.error.validation") + " " + exception.getMessage());
        }
    }

    private void removeProduct() {
        String idText = JOptionPane.showInputDialog(this, messages.get("product.remove.prompt.id"));
        if (idText == null) {
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idText.trim());
        } catch (Exception exception) {
            showMessage(messages.get("product.remove.invalidId"));
            return;
        }

        Product removed = productModel.removeProductById(id);
        if (removed == null) {
            showMessage(messages.get("product.remove.notFound"));
            return;
        }

        showMessage(messages.get("product.remove.success") + " id=" + removed.getId() + " | " + removed.getDescription());
        refreshProductTable();
    }

    private void refreshProductTable() {
        productTableModel.setRowCount(0);
        List<Product> products = productModel.getProducts();
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            productTableModel.addRow(new Object[] {
                Integer.valueOf(product.getId()),
                product.getDescription(),
                product.getUnit(),
                product.getPrice().toPlainString()
            });
        }
    }

    private void exportProductsToCsv() {
        List<Product> products = productModel.getProducts();
        if (products.isEmpty()) {
            showMessage(messages.get("product.export.empty"));
            return;
        }

        File file = chooseExportFile("productos.csv");
        if (file == null) {
            return;
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("id,descripcion,unidad,precio");
            writer.newLine();
            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                writer.write(product.getId() + "," + escapeCsv(product.getDescription()) + ","
                        + escapeCsv(product.getUnit()) + "," + product.getPrice().toPlainString());
                writer.newLine();
            }
            showMessage(messages.get("product.export.success") + " " + file.getAbsolutePath());
        } catch (IOException exception) {
            showMessage(messages.get("product.export.error") + " " + exception.getMessage());
        } finally {
            closeQuietly(writer);
        }
    }

    private void addAccountingMovement() {
        JTextField descriptionField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<String>(new String[] { "Ingreso", "Egreso" });
        JTextField valueField = new JTextField();
        JTextField dateTimeField = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel(messages.get("accounting.prompt.description")));
        form.add(descriptionField);
        form.add(new JLabel(messages.get("accounting.prompt.type")));
        form.add(typeCombo);
        form.add(new JLabel(messages.get("accounting.prompt.value")));
        form.add(valueField);
        form.add(new JLabel(messages.get("accounting.prompt.datetime")));
        form.add(dateTimeField);

        int result = JOptionPane.showConfirmDialog(this, form, messages.get("menu.accounting.add"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        BigDecimal value;
        try {
            value = new BigDecimal(safeTrim(valueField.getText()));
        } catch (Exception exception) {
            showMessage(messages.get("accounting.error.value"));
            return;
        }

        String dateTime = safeTrim(dateTimeField.getText());
        if (!isValidDateTime(dateTime)) {
            showMessage(messages.get("accounting.error.datetime"));
            return;
        }

        try {
            accountingModel.createMovement(safeTrim(descriptionField.getText()), String.valueOf(typeCombo.getSelectedItem()),
                    value, dateTime);
            showMessage(messages.get("accounting.success.created"));
            refreshAccountingTable();
        } catch (IllegalArgumentException exception) {
            showMessage(messages.get("accounting.error.validation") + " " + exception.getMessage());
        }
    }

    private void refreshAccountingTable() {
        accountingTableModel.setRowCount(0);
        List<AccountingMovement> movements = accountingModel.getMovementsLifo();

        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < movements.size(); i++) {
            AccountingMovement movement = movements.get(i);
            accountingTableModel.addRow(new Object[] {
                Integer.valueOf(movement.getId()),
                movement.getDescription(),
                movement.getMovementType(),
                movement.getValue().toPlainString(),
                movement.getDateTime()
            });

            if ("Ingreso".equalsIgnoreCase(movement.getMovementType())) {
                total = total.add(movement.getValue());
            } else {
                total = total.subtract(movement.getValue());
            }
        }

        accountingTotalLabel.setText(messages.get("accounting.list.total") + " " + total.toPlainString());
    }

    private void exportAccountingToCsv() {
        List<AccountingMovement> movements = accountingModel.getMovementsLifo();
        if (movements.isEmpty()) {
            showMessage(messages.get("accounting.export.empty"));
            return;
        }

        File file = chooseExportFile("contabilidad.csv");
        if (file == null) {
            return;
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
            showMessage(messages.get("accounting.export.success") + " " + file.getAbsolutePath());
        } catch (IOException exception) {
            showMessage(messages.get("accounting.export.error") + " " + exception.getMessage());
        } finally {
            closeQuietly(writer);
        }
    }

    private void setTableLeftAlignment(JTable table) {
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        for (int index = 0; index < table.getColumnCount(); index++) {
            table.getColumnModel().getColumn(index).setCellRenderer(leftRenderer);
        }
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
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return chooser.getSelectedFile();
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private String safeTrim(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
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

    private int calculateAge(String birthDate) {
        try {
            LocalDate birth = LocalDate.parse(birthDate, BIRTH_DATE_FORMATTER);
            return Period.between(birth, LocalDate.now()).getYears();
        } catch (DateTimeParseException exception) {
            return 0;
        }
    }

    private void closeQuietly(BufferedWriter writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException ignored) {
                showMessage("No se pudo cerrar archivo.");
            }
        }
    }
}
