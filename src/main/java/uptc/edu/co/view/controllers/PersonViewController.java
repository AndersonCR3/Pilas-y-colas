package uptc.edu.co.view.controllers;

import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import uptc.edu.co.i18n.MessageService;
import uptc.edu.co.mediator.GuiMenuMediator;
import uptc.edu.co.pojo.Person;
import uptc.edu.co.presenter.ActionResult;
import uptc.edu.co.structures.DoubleList;

public class PersonViewController extends AbstractViewController {
    private static final DateTimeFormatter BIRTH_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final GuiMenuMediator menuMediator;

    public PersonViewController(JFrame parent, MessageService messages, GuiMenuMediator menuMediator) {
        super(parent, messages);
        this.menuMediator = menuMediator;
    }

    public void addPerson() {
        PersonFormData data = readPersonFormData();
        if (data == null) {
            return;
        }
        ActionResult result = menuMediator.registerPerson(data.names, data.lastNames, data.gender, data.birthDate);
        showMessage(result.getMessage());
    }

    public void removePersonByParameter() {
        String param = readInput("person.remove.prompt.parameter");
        if (isBlank(param)) {
            return;
        }
        ActionResult result = menuMediator.removePersonByParameter(param.trim());
        showMessage(result.getMessage());
    }

    public void removeLastPerson() {
        ActionResult result = menuMediator.removeLastPerson();
        showMessage(result.getMessage());
    }

    public void openPersonListWindow() {
        PersonListContext context = createPersonListContext();
        context.prevButton.addActionListener(event -> goPreviousPersonPage(context));
        context.nextButton.addActionListener(event -> goNextPersonPage(context));
        refreshPersonList(context);
        showListWindow(context.frame, context.table, context.bottomPanel);
    }

    public void exportPeopleToCsv() {
        DoubleList<Person> people = menuMediator.getPeople();
        if (people.isEmpty()) {
            showMessage(messages.get("person.export.empty"));
            return;
        }
        File file = chooseExportFile("personas.csv");
        if (file != null) {
            showMessage(menuMediator.exportPeople(file).getMessage());
        }
    }

    private PersonFormData readPersonFormData() {
        JTextField namesField = new JTextField();
        JTextField lastNamesField = new JTextField();
        JComboBox<String> genderCombo = new JComboBox<String>(
            new String[] { messages.get("option.gender.male"), messages.get("option.gender.female") });
        JTextField birthDateField = new JTextField();
        JPanel form = createFormPanel();
        addFormRow(form, "person.prompt.names", namesField);
        addFormRow(form, "person.prompt.lastNames", lastNamesField);
        addFormRow(form, "person.prompt.gender", genderCombo);
        addFormRow(form, "person.prompt.birthDate", birthDateField);
        if (!confirmForm(form, "menu.person.add")) {
            return null;
        }
        String canonicalGender = toCanonicalGender(String.valueOf(genderCombo.getSelectedItem()));
        return new PersonFormData(safeTrim(namesField.getText()), safeTrim(lastNamesField.getText()),
            canonicalGender, safeTrim(birthDateField.getText()));
    }

    private PersonListContext createPersonListContext() {
        JFrame frame = createListFrame("menu.person.list");
        DefaultTableModel model = nonEditableModel(new Object[] { messages.get("person.list.col.names"),
                messages.get("person.list.col.lastNames"), messages.get("person.list.col.gender"),
                messages.get("person.list.col.age") });
        JTable table = createAlignedTable(model);
        JPanel bottomPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        JLabel pageLabel = new JLabel("1/1");
        int[] pageIndex = new int[] { 0 };
        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");
        bottomPanel.add(prevButton);
        bottomPanel.add(pageLabel);
        bottomPanel.add(nextButton);
        return new PersonListContext(frame, table, model, bottomPanel, pageLabel, pageIndex, prevButton, nextButton);
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
        DoubleList<Person> people = menuMediator.getPeople();
        int totalPages = adjustPersonPageIndex(context, people);
        addPersonRows(context.model, people, context.pageIndex[0], menuMediator.getPersonPageSize());
        context.pageLabel.setText((context.pageIndex[0] + 1) + "/" + totalPages);
    }

    private int adjustPersonPageIndex(PersonListContext context, DoubleList<Person> people) {
        int totalPages = calculatePages(people.size(), menuMediator.getPersonPageSize());
        if (context.pageIndex[0] >= totalPages) {
            context.pageIndex[0] = Math.max(0, totalPages - 1);
        }
        return totalPages;
    }

    private void addPersonRows(DefaultTableModel model, DoubleList<Person> people, int pageIndex, int pageSize) {
        int start = pageIndex * pageSize;
        int end = Math.min(start + pageSize, people.size());
        for (int index = start; index < end; index++) {
            Person person = people.get(index);
            model.addRow(new Object[] { person.getNames(), person.getLastNames(), localizeGender(person.getGender()),
                    Integer.valueOf(calculateAge(person.getBirthDate())) });
        }
    }

    private String toCanonicalGender(String selectedLabel) {
        if (messages.get("option.gender.male").equals(selectedLabel)) {
            return "Masculino";
        }
        if (messages.get("option.gender.female").equals(selectedLabel)) {
            return "Femenino";
        }
        return safeTrim(selectedLabel);
    }

    private String localizeGender(String canonicalGender) {
        if ("Masculino".equalsIgnoreCase(canonicalGender)) {
            return messages.get("option.gender.male");
        }
        if ("Femenino".equalsIgnoreCase(canonicalGender)) {
            return messages.get("option.gender.female");
        }
        return canonicalGender;
    }

    private int totalPersonPages() {
        return calculatePages(menuMediator.getPeople().size(), menuMediator.getPersonPageSize());
    }

    private int calculateAge(String birthDate) {
        try {
            LocalDate birth = LocalDate.parse(birthDate, BIRTH_DATE_FORMATTER);
            return Period.between(birth, LocalDate.now()).getYears();
        } catch (DateTimeParseException exception) {
            return 0;
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

    private static final class PersonListContext {
        private final JFrame frame;
        private final JTable table;
        private final DefaultTableModel model;
        private final JPanel bottomPanel;
        private final JLabel pageLabel;
        private final int[] pageIndex;
        private final JButton prevButton;
        private final JButton nextButton;

        private PersonListContext(JFrame frame, JTable table, DefaultTableModel model, JPanel bottomPanel,
                JLabel pageLabel, int[] pageIndex, JButton prevButton, JButton nextButton) {
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
