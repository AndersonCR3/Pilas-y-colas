package uptc.edu.co.pojo;

public class Person {
    private final int id;
    private final String names;
    private final String lastNames;
    private final String gender;
    private final String birthDate;

    public Person(int id, String names, String lastNames, String gender, String birthDate) {
        this.id = id;
        this.names = names;
        this.lastNames = lastNames;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public int getId() {
        return id;
    }

    public String getNames() {
        return names;
    }

    public String getLastNames() {
        return lastNames;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    @Override
    public String toString() {
        return "id=" + id
                + " | nombres=" + names
                + " | apellidos=" + lastNames
                + " | genero=" + gender
                + " | fechaNacimiento=" + birthDate;
    }
}
