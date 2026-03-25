package uptc.edu.co.presenter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uptc.edu.co.i18n.MessageService;
import uptc.edu.co.interfaces.IPersonModel;
import uptc.edu.co.pojo.Person;
import uptc.edu.co.view.PersonView;

public class PersonPresenter {
        private static final DateTimeFormatter BIRTH_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final Logger LOGGER = LogManager.getLogger(PersonPresenter.class);

    private final IPersonModel personModel;
    private final PersonView personView;
    private final MessageService messages;

    public PersonPresenter(IPersonModel personModel, PersonView personView, MessageService messages) {
        this.personModel = personModel;
        this.personView = personView;
        this.messages = messages;
    }

    public void registerPerson() {
        String names = safeTrim(personView.readText(messages.get("person.prompt.names")));
        String lastNames = safeTrim(personView.readText(messages.get("person.prompt.lastNames")));
        String gender = safeTrim(personView.readText(messages.get("person.prompt.gender")));
        String birthDate = safeTrim(personView.readText(messages.get("person.prompt.birthDate")));

        if (!isValidLength(names, personModel.getMinNamesLength(), personModel.getMaxNamesLength())) {
            personView.showMessage(messages.get("person.error.namesLength")
                    + " " + personModel.getMinNamesLength() + " - " + personModel.getMaxNamesLength());
            return;
        }

        if (!isValidLength(lastNames, personModel.getMinLastNamesLength(), personModel.getMaxLastNamesLength())) {
            personView.showMessage(messages.get("person.error.lastNamesLength")
                    + " " + personModel.getMinLastNamesLength() + " - " + personModel.getMaxLastNamesLength());
            return;
        }

        if (!isValidGender(gender)) {
            personView.showMessage(messages.get("person.error.gender"));
            return;
        }

        if (!isValidBirthDate(birthDate)) {
            personView.showMessage(messages.get("person.error.birthDate"));
            return;
        }

        Person person = personModel.createPerson(names, lastNames, normalizeGender(gender), birthDate);
        personView.showMessage(messages.get("person.success.created") + " " + person.getId());
        LOGGER.info("Persona registrada con id {}", Integer.valueOf(person.getId()));
    }

    public void listPeople() {
        List<Person> people = personModel.getPeople();
        if (people.isEmpty()) {
            personView.showMessage(messages.get("person.list.empty"));
            return;
        }

        personView.showMessage("\n" + messages.get("person.list.header"));
        personView.showMessage(String.format("%-20s %-20s %-12s %-5s", messages.get("person.list.col.names"),
                messages.get("person.list.col.lastNames"), messages.get("person.list.col.gender"),
                messages.get("person.list.col.age")));
        int pageSize = personModel.getPageSize();

        for (int start = 0; start < people.size(); start += pageSize) {
            int end = start + pageSize;
            if (end > people.size()) {
                end = people.size();
            }

            for (int index = start; index < end; index++) {
                Person person = people.get(index);
                int age = calculateAge(person.getBirthDate());
                personView.showMessage(String.format("%-20s %-20s %-12s %-5d", person.getNames(),
                    person.getLastNames(), person.getGender(), Integer.valueOf(age)));
            }

            if (end < people.size()) {
                String nextPagePrompt = messages.get("person.list.nextPage").replace("{pageSize}",
                        String.valueOf(pageSize));
                String answer = safeTrim(personView.readText(nextPagePrompt));
                if (!"s".equalsIgnoreCase(answer) && !"si".equalsIgnoreCase(answer)) {
                    break;
                }
            }
        }
    }

    public void exportPeopleToCsv() {
        List<Person> people = personModel.getPeople();
        if (people.isEmpty()) {
            personView.showMessage(messages.get("person.export.empty"));
            return;
        }

        String fileName = personView.readText(messages.get("person.export.fileName"));
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "personas.csv";
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileName.trim()));
            writer.write("id,nombres,apellidos,genero,fecha_nacimiento");
            writer.newLine();

            for (int index = 0; index < people.size(); index++) {
                Person person = people.get(index);
                writer.write(person.getId()
                        + "," + escapeCsv(person.getNames())
                        + "," + escapeCsv(person.getLastNames())
                        + "," + escapeCsv(person.getGender())
                        + "," + escapeCsv(person.getBirthDate()));
                writer.newLine();
            }

            personView.showMessage(messages.get("person.export.success") + " " + fileName.trim());
            LOGGER.info("Archivo CSV generado: {}", fileName.trim());
        } catch (IOException exception) {
            personView.showMessage(messages.get("person.export.error") + " " + exception.getMessage());
            LOGGER.error("Error exportando CSV", exception);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                    LOGGER.warn("No se pudo cerrar el archivo CSV.");
                }
            }
        }
    }

    public void removePersonFromQueue() {
        Person removed = personModel.removeNextPerson();
        if (removed == null) {
            personView.showMessage(messages.get("person.remove.empty"));
            return;
        }

        personView.showMessage(messages.get("person.remove.success") + " " + removed.getNames() + " "
                + removed.getLastNames());
        LOGGER.info("Persona retirada de la cola: id {}", Integer.valueOf(removed.getId()));
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

    private boolean isValidGender(String gender) {
        String normalizedGender = normalizeGender(gender);
        return "Masculino".equals(normalizedGender) || "Femenino".equals(normalizedGender);
    }

    private String normalizeGender(String gender) {
        if (gender == null) {
            return "";
        }
        String lower = gender.trim().toLowerCase();
        if ("masculino".equals(lower)) {
            return "Masculino";
        }
        if ("femenino".equals(lower)) {
            return "Femenino";
        }
        return gender.trim();
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

    private int calculateAge(String birthDate) {
        try {
            LocalDate birth = LocalDate.parse(birthDate, BIRTH_DATE_FORMATTER);
            return Period.between(birth, LocalDate.now()).getYears();
        } catch (DateTimeParseException exception) {
            return 0;
        }
    }
}
