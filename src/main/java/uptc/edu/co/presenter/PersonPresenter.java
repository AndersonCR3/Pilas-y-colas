package uptc.edu.co.presenter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import uptc.edu.co.i18n.MessageService;
import uptc.edu.co.interfaces.IPersonModel;
import uptc.edu.co.pojo.Person;

public class PersonPresenter {
    private final IPersonModel personModel;
    private final MessageService messages;

    public PersonPresenter(IPersonModel personModel, MessageService messages) {
        this.personModel = personModel;
        this.messages = messages;
    }

    public ActionResult registerPerson(String names, String lastNames, String gender, String birthDate) {
        if (!isValidLength(names, personModel.getMinNamesLength(), personModel.getMaxNamesLength())) {
            return ActionResult.failure(messages.get("person.error.namesLength") + " " + personModel.getMinNamesLength()
                    + " - " + personModel.getMaxNamesLength());
        }
        if (!isValidLength(lastNames, personModel.getMinLastNamesLength(), personModel.getMaxLastNamesLength())) {
            return ActionResult.failure(messages.get("person.error.lastNamesLength") + " "
                    + personModel.getMinLastNamesLength() + " - " + personModel.getMaxLastNamesLength());
        }
        if (!isValidGender(gender)) {
            return ActionResult.failure(messages.get("person.error.gender"));
        }
        if (!isValidBirthDate(birthDate)) {
            return ActionResult.failure(messages.get("person.error.birthDate"));
        }
        Person person = personModel.createPerson(names, lastNames, normalizeGender(gender), birthDate);
        return ActionResult.success(messages.get("person.success.created") + " " + person.getId());
    }

    public ActionResult removePersonByParameter(String param) {
        Person removed = removeByParam(param);
        if (removed == null) {
            return ActionResult.failure(messages.get("person.remove.notFound"));
        }
        String message = messages.get("person.remove.success") + " " + removed.getNames() + " " + removed.getLastNames();
        return ActionResult.success(message);
    }

    public ActionResult removeLastPerson() {
        Person removed = personModel.removeLastPerson();
        if (removed == null) {
            return ActionResult.failure(messages.get("person.remove.empty"));
        }
        String message = messages.get("person.removeLast.success") + " " + removed.getNames() + " " + removed.getLastNames();
        return ActionResult.success(message);
    }

    public List<Person> getPeople() {
        return personModel.getPeople();
    }

    public int getPageSize() {
        return personModel.getPageSize();
    }

    public ActionResult exportPeople(File file) {
        List<Person> people = personModel.getPeople();
        if (people.isEmpty()) {
            return ActionResult.failure(messages.get("person.export.empty"));
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
            return ActionResult.success(messages.get("person.export.success") + " " + file.getAbsolutePath());
        } catch (IOException exception) {
            return ActionResult.failure(messages.get("person.export.error") + " " + exception.getMessage());
        } finally {
            closeQuietly(writer);
        }
    }

    private Person removeByParam(String param) {
        try {
            return personModel.removePersonById(Integer.parseInt(param));
        } catch (NumberFormatException exception) {
            return personModel.removePersonByName(param);
        }
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
