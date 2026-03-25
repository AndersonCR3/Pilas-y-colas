package uptc.edu.co.interfaces;

import java.util.List;

import uptc.edu.co.pojo.Person;

public interface IPersonModel {
    void addPerson(Person person);

    Person createPerson(String names, String lastNames, String gender, String birthDate);

    Person removeNextPerson();

    List<Person> getPeople();

    int getMinNamesLength();

    int getMaxNamesLength();

    int getMinLastNamesLength();

    int getMaxLastNamesLength();

    void setNamesLengthRange(int minLength, int maxLength);

    void setLastNamesLengthRange(int minLength, int maxLength);

    int getPageSize();
}