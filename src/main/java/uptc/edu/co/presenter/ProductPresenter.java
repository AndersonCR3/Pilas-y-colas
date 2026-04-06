package uptc.edu.co.presenter;

import java.io.File;
import java.math.BigDecimal;

import uptc.edu.co.mediator.GuiMenuMediator;
import uptc.edu.co.pojo.Product;
import uptc.edu.co.structures.DoubleList;

public class ProductPresenter {
    private final GuiMenuMediator mediator;

    public ProductPresenter(GuiMenuMediator mediator) {
        this.mediator = mediator;
    }

    public ActionResult registerProduct(String description, String unit, BigDecimal quantity, BigDecimal price) {
        return mediator.registerProduct(description, unit, quantity, price);
    }

    public ActionResult removeProductByParameter(String param) {
        return mediator.removeProductByParameter(param);
    }

    public DoubleList<Product> getProducts() {
        return mediator.getProducts();
    }

    public ActionResult exportProducts(File file) {
        return mediator.exportProducts(file);
    }
}
