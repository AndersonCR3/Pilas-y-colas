package uptc.edu.co.presenter;

import java.io.File;

import uptc.edu.co.mediator.GuiMenuMediator;
import uptc.edu.co.pojo.Person;
import uptc.edu.co.structures.DoubleList;

public class PersonPresenter {
    private final GuiMenuMediator mediator;

    public PersonPresenter(GuiMenuMediator mediator) {
        this.mediator = mediator;
    }

    public ActionResult registerPerson(String names, String lastNames, String gender, String birthDate) {
        return mediator.registerPerson(names, lastNames, gender, birthDate);
    }

    public ActionResult removePersonByParameter(String param) {
        return mediator.removePersonByParameter(param);
    }

    public ActionResult removeLastPerson() {
        return mediator.removeLastPerson();
    }

    public DoubleList<Person> getPeople() {
        return mediator.getPeople();
    }

    public int getPersonPageSize() {
        return mediator.getPersonPageSize();
    }

    public ActionResult exportPeople(File file) {
        return mediator.exportPeople(file);
    }
}
