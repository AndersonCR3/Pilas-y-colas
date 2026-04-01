package uptc.edu.co.interfaces;

import java.math.BigDecimal;

import uptc.edu.co.pojo.Product;
import uptc.edu.co.structures.DoubleList;

public interface IProductModel {
    Product createProduct(String description, String unit, BigDecimal quantity, BigDecimal price);

    Product removeProductById(int id);

    Product removeProductByName(String description);

    DoubleList<Product> getProducts();

    BigDecimal getMaxPrice();
}
