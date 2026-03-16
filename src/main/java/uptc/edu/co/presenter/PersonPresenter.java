package uptc.edu.co.presenter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import uptc.edu.co.model.Person;
import uptc.edu.co.model.PersonModel;
import uptc.edu.co.view.PersonView;

public class PersonPresenter {
    private final PersonModel personModel;
    private final PersonView personView;

    public PersonPresenter(PersonModel personModel, PersonView personView) {
        this.personModel = personModel;
        this.personView = personView;
    }

    public void registerPerson() {
        String names = safeTrim(personView.readText("Ingrese nombres: "));
        String lastNames = safeTrim(personView.readText("Ingrese apellidos: "));
        String gender = safeTrim(personView.readText("Ingrese genero (Masculino/Femenino): "));
        String birthDate = safeTrim(personView.readText("Ingrese fecha de nacimiento (dd/MM/yyyy): "));

        if (!isValidLength(names, personModel.getMinNamesLength(), personModel.getMaxNamesLength())) {
            personView.showMessage("Nombres invalidos. Longitud permitida: "
                    + personModel.getMinNamesLength() + " a " + personModel.getMaxNamesLength() + ".");
            return;
        }

        if (!isValidLength(lastNames, personModel.getMinLastNamesLength(), personModel.getMaxLastNamesLength())) {
            personView.showMessage("Apellidos invalidos. Longitud permitida: "
                    + personModel.getMinLastNamesLength() + " a " + personModel.getMaxLastNamesLength() + ".");
            return;
        }

        if (!isValidGender(gender)) {
            personView.showMessage("Genero invalido. Opciones permitidas: Masculino o Femenino.");
            return;
        }

        if (!isValidBirthDate(birthDate)) {
            personView.showMessage("Fecha de nacimiento invalida. Use formato dd/MM/yyyy.");
            return;
        }

        Person person = personModel.createPerson(names, lastNames, normalizeGender(gender), birthDate);
        if (person == null) {
            personView.showMessage("Datos invalidos. No se registro la persona.");
            return;
        }

        personView.showMessage("Persona registrada correctamente con id: " + person.getId());
    }

    public void listPeople() {
        List<Person> people = personModel.getPeople();
        if (people.isEmpty()) {
            personView.showMessage("No hay personas registradas.");
            return;
        }

        personView.showMessage("\nPersonas registradas (atributos: id, nombres, apellidos, genero, fechaNacimiento):");
        for (int index = 0; index < people.size(); index++) {
            Person person = people.get(index);
            personView.showMessage((index + 1) + ". id=" + person.getId()
                    + " | nombres=" + person.getNames()
                    + " | apellidos=" + person.getLastNames()
                    + " | genero=" + person.getGender()
                    + " | fechaNacimiento=" + person.getBirthDate());
        }
    }

    public void exportPeopleToCsv() {
        List<Person> people = personModel.getPeople();
        if (people.isEmpty()) {
            personView.showMessage("No hay personas para exportar.");
            return;
        }

        String fileName = personView.readText("Ingrese nombre de archivo CSV (ej: personas.csv): ");
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "personas.csv";
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileName.trim()));
            writer.write("id,nombres,apellidos,genero,fecha_nacimiento");
            writer.newLine();

            for (Person person : people) {
                writer.write(person.getId()
                        + "," + escapeCsv(person.getNames())
                        + "," + escapeCsv(person.getLastNames())
                        + "," + escapeCsv(person.getGender())
                        + "," + escapeCsv(person.getBirthDate()));
                writer.newLine();
            }

            personView.showMessage("Archivo CSV exportado correctamente: " + fileName.trim());
        } catch (IOException exception) {
            personView.showMessage("Error al exportar archivo CSV: " + exception.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                    personView.showMessage("No se pudo cerrar el archivo CSV correctamente.");
                }
            }
        }
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
}
