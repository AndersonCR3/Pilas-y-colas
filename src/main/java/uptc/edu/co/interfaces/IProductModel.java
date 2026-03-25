package uptc.edu.co.interfaces;

import java.math.BigDecimal;
import java.util.List;

import uptc.edu.co.pojo.Product;

public interface IProductModel {
    Product createProduct(String description, String unit, BigDecimal price);

    Product removeProductById(int id);

    List<Product> getProducts();

    BigDecimal getMaxPrice();
}
