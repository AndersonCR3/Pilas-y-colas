package uptc.edu.co.model;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import uptc.edu.co.config.AppConfig;
import uptc.edu.co.interfaces.IPersonModel;
import uptc.edu.co.pojo.Person;

public class PersonModel implements IPersonModel {
    private final Queue<Person> peopleQueue;
    private int nextId;
    private int minNamesLength;
    private int maxNamesLength;
    private int minLastNamesLength;
    private int maxLastNamesLength;
    private int pageSize;

    public PersonModel(AppConfig appConfig) {
        this.peopleQueue = new ArrayDeque<Person>();
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
        peopleQueue.offer(person);
    }

    public Person createPerson(String names, String lastNames, String gender, String birthDate) {
        Person person = new Person(nextId, names, lastNames, gender, birthDate);
        nextId++;
        addPerson(person);
        return person;
    }

    public Person removeNextPerson() {
        return peopleQueue.poll();
    }

    public List<Person> getPeople() {
        return Collections.unmodifiableList(new ArrayList<Person>(peopleQueue));
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
