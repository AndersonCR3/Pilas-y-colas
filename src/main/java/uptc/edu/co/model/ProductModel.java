package uptc.edu.co.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import uptc.edu.co.config.AppConfig;
import uptc.edu.co.interfaces.IProductModel;
import uptc.edu.co.pojo.Product;

public class ProductModel implements IProductModel {
    private final List<Product> products;
    private final BigDecimal maxPrice;
    private final String descriptionFormat;
    private int nextId;

    public ProductModel(AppConfig appConfig) {
        this.products = new ArrayList<Product>();
        this.nextId = 1;
        this.maxPrice = new BigDecimal(appConfig.getString("product.price.max", "99999999.99"));
        this.descriptionFormat = appConfig.getString("product.description.format", "UPPER");
    }

    public Product createProduct(String description, String unit, BigDecimal price) {
        String normalizedDescription = normalizeDescription(description);
        String normalizedUnit = normalizeUnit(unit);

        validateDescription(normalizedDescription);
        validateUnit(normalizedUnit);
        validatePrice(price);

        Product product = new Product(nextId, normalizedDescription, normalizedUnit, price);
        nextId++;
        products.add(product);
        return product;
    }

    public Product removeProductById(int id) {
        for (int index = 0; index < products.size(); index++) {
            Product current = products.get(index);
            if (current.getId() == id) {
                products.remove(index);
                return current;
            }
        }
        return null;
    }

    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    private void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Descripcion vacia");
        }

        String expected = applyFormat(description.trim());
        if (!expected.equals(description.trim())) {
            throw new IllegalArgumentException("Descripcion invalida para formato configurado");
        }
    }

    private void validateUnit(String unit) {
        if (!"libra".equals(unit) && !"kilos".equals(unit) && !"bultos".equals(unit) && !"toneladas".equals(unit)) {
            throw new IllegalArgumentException("Unidad invalida");
        }
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Precio debe ser positivo");
        }
        if (price.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Precio supera maximo permitido");
        }
    }

    private String normalizeDescription(String description) {
        if (description == null) {
            return "";
        }
        return applyFormat(description.trim());
    }

    private String applyFormat(String value) {
        if ("UPPER".equalsIgnoreCase(descriptionFormat)) {
            return value.toUpperCase(Locale.ROOT);
        }
        if ("TITLE".equalsIgnoreCase(descriptionFormat)) {
            String[] words = value.toLowerCase(Locale.ROOT).split("\\s+");
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                if (word.isEmpty()) {
                    continue;
                }
                builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
                if (i < words.length - 1) {
                    builder.append(' ');
                }
            }
            return builder.toString();
        }
        return value.toUpperCase(Locale.ROOT);
    }

    private String normalizeUnit(String unit) {
        if (unit == null) {
            return "";
        }
        return unit.trim().toLowerCase(Locale.ROOT);
    }
}
