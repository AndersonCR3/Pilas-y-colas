package uptc.edu.co.pojo;

import java.math.BigDecimal;

public class Product {
    private final int id;
    private final String description;
    private final String unit;
    private final BigDecimal quantity;
    private final BigDecimal price;

    public Product(int id, String description, String unit, BigDecimal quantity, BigDecimal price) {
        this.id = id;
        this.description = description;
        this.unit = unit;
        this.quantity = quantity;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getUnit() {
        return unit;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
