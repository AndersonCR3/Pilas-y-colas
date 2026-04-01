package uptc.edu.co.model;

import uptc.edu.co.config.AppConfig;
import uptc.edu.co.interfaces.IPersonModel;
import uptc.edu.co.pojo.Person;
import uptc.edu.co.structures.CollectionMode;
import uptc.edu.co.structures.DoubleList;
import uptc.edu.co.structures.ManagerCollection;

public class PersonModel implements IPersonModel {
    private final ManagerCollection<Person> people;
    private int nextId;
    private int minNamesLength;
    private int maxNamesLength;
    private int minLastNamesLength;
    private int maxLastNamesLength;
    private int pageSize;

    public PersonModel(AppConfig appConfig) {
        CollectionMode mode = CollectionMode.from(appConfig.getString("person.collection.mode", "QUEUE"),
            CollectionMode.QUEUE);
        this.people = new ManagerCollection<Person>(mode);
        this.nextId = 1;
        this.minNamesLength = appConfig.getInt("person.names.min.length", 2);
        this.maxNamesLength = appConfig.getInt("person.names.max.length", 40);
        this.minLastNamesLength = appConfig.getInt("person.lastnames.min.length", 2);
        this.maxLastNamesLength = appConfig.getInt("person.lastnames.max.length", 40);
        this.pageSize = appConfig.getInt("person.list.page.size", 10);

        if (pageSize <= 0) {
            pageSize = 10;
        }
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

    public Person removeNextPerson() {
        return people.remove();
    }

    public Person removePersonById(int id) {
        for (int index = 0; index < people.size(); index++) {
            Person person = people.get(index);
            if (person != null && person.getId() == id) {
                return people.removeAt(index);
            }
        }
        return null;
    }

    public Person removePersonByName(String names) {
        if (names == null) {
            return null;
        }
        String targetName = names.trim();
        for (int index = 0; index < people.size(); index++) {
            Person person = people.get(index);
            if (person != null && person.getNames().equalsIgnoreCase(targetName)) {
                return people.removeAt(index);
            }
        }
        return null;
    }

    public Person removeLastPerson() {
        return people.removeLast();
    }

    public DoubleList<Person> getPeople() {
        return people.getOrdered();
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

    public int getPageSize() {
        return pageSize;
    }
}
