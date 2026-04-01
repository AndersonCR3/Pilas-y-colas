package uptc.edu.co.interfaces;

import uptc.edu.co.pojo.Person;
import uptc.edu.co.structures.DoubleList;

public interface IPersonModel {
    void addPerson(Person person);

    Person createPerson(String names, String lastNames, String gender, String birthDate);

    Person removeNextPerson();

    Person removePersonById(int id);

    Person removePersonByName(String names);

    Person removeLastPerson();

    DoubleList<Person> getPeople();

    int getMinNamesLength();

    int getMaxNamesLength();

    int getMinLastNamesLength();

    int getMaxLastNamesLength();

    void setNamesLengthRange(int minLength, int maxLength);

    void setLastNamesLengthRange(int minLength, int maxLength);

    int getPageSize();
}