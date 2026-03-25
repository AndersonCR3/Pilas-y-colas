package uptc.edu.co.interfaces;

import java.math.BigDecimal;
import java.util.List;

import uptc.edu.co.pojo.Product;

public interface IProductModel {
    Product createProduct(String description, String unit, BigDecimal quantity, BigDecimal price);

    Product removeProductById(int id);

    Product removeProductByName(String description);

    List<Product> getProducts();

    BigDecimal getMaxPrice();
}
