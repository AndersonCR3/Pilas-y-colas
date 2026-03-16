package uptc.edu.co.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PersonModel {
    private final List<Person> people;
    private int nextId;
    private int minNamesLength;
    private int maxNamesLength;
    private int minLastNamesLength;
    private int maxLastNamesLength;

    public PersonModel() {
        this.people = new ArrayList<Person>();
        this.nextId = 1;
        this.minNamesLength = 2;
        this.maxNamesLength = 40;
        this.minLastNamesLength = 2;
        this.maxLastNamesLength = 40;
    }

    public void addPerson(Person person) {
        people.add(person);
    }

    public Person createPerson(String names, String lastNames, String gender, String birthDate) {
        Person person = new Person(nextId, names, lastNames, gender, birthDate);
        nextId++;
        addPerson(person);
        return person;
    }

    public List<Person> getPeople() {
        return Collections.unmodifiableList(people);
    }

    public int getMinNamesLength() {
        return minNamesLength;
    }

    public int getMaxNamesLength() {
        return maxNamesLength;
    }

    public int getMinLastNamesLength() {
        return minLastNamesLength;
    }

    public int getMaxLastNamesLength() {
        return maxLastNamesLength;
    }

    public void setNamesLengthRange(int minLength, int maxLength) {
        if (minLength > 0 && maxLength >= minLength) {
            this.minNamesLength = minLength;
            this.maxNamesLength = maxLength;
        }
    }

    public void setLastNamesLengthRange(int minLength, int maxLength) {
        if (minLength > 0 && maxLength >= minLength) {
            this.minLastNamesLength = minLength;
            this.maxLastNamesLength = maxLength;
        }
    }
}
