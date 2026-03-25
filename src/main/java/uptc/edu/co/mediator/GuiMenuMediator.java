package uptc.edu.co.mediator;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import uptc.edu.co.pojo.AccountingMovement;
import uptc.edu.co.pojo.Person;
import uptc.edu.co.pojo.Product;
import uptc.edu.co.presenter.AccountingPresenter;
import uptc.edu.co.presenter.ActionResult;
import uptc.edu.co.presenter.PersonPresenter;
import uptc.edu.co.presenter.ProductPresenter;

public class GuiMenuMediator {
    private final PersonPresenter personPresenter;
    private final ProductPresenter productPresenter;
    private final AccountingPresenter accountingPresenter;

    public GuiMenuMediator(PersonPresenter personPresenter, ProductPresenter productPresenter,
            AccountingPresenter accountingPresenter) {
        this.personPresenter = personPresenter;
        this.productPresenter = productPresenter;
        this.accountingPresenter = accountingPresenter;
    }

    public ActionResult registerPerson(String names, String lastNames, String gender, String birthDate) {
        return personPresenter.registerPerson(names, lastNames, gender, birthDate);
    }

    public ActionResult removePersonByParameter(String param) {
        return personPresenter.removePersonByParameter(param);
    }

    public ActionResult removeLastPerson() {
        return personPresenter.removeLastPerson();
    }

    public List<Person> getPeople() {
        return personPresenter.getPeople();
    }

    public int getPersonPageSize() {
        return personPresenter.getPageSize();
    }

    public ActionResult exportPeople(File file) {
        return personPresenter.exportPeople(file);
    }

    public ActionResult registerProduct(String description, String unit, BigDecimal quantity, BigDecimal price) {
        return productPresenter.registerProduct(description, unit, quantity, price);
    }

    public ActionResult removeProductByParameter(String param) {
        return productPresenter.removeProductByParameter(param);
    }

    public List<Product> getProducts() {
        return productPresenter.getProducts();
    }

    public ActionResult exportProducts(File file) {
        return productPresenter.exportProducts(file);
    }

    public ActionResult registerMovement(String description, String type, BigDecimal value, String dateTime) {
        return accountingPresenter.registerMovement(description, type, value, dateTime);
    }

    public List<AccountingMovement> getMovementsLifo() {
        return accountingPresenter.getMovementsLifo();
    }

    public BigDecimal calculateAccountingTotal(List<AccountingMovement> movements) {
        return accountingPresenter.calculateTotal(movements);
    }

    public ActionResult exportAccounting(File file) {
        return accountingPresenter.exportMovements(file);
    }
}
