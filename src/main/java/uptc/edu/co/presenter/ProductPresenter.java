package uptc.edu.co.presenter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import uptc.edu.co.i18n.MessageService;
import uptc.edu.co.interfaces.IProductModel;
import uptc.edu.co.pojo.Product;

public class ProductPresenter {
    private final IProductModel productModel;
    private final MessageService messages;

    public ProductPresenter(IProductModel productModel, MessageService messages) {
        this.productModel = productModel;
        this.messages = messages;
    }

    public ActionResult registerProduct(String description, String unit, BigDecimal quantity, BigDecimal price) {
        try {
            Product product = productModel.createProduct(description, unit, quantity, price);
            return ActionResult.success(messages.get("product.success.created") + " " + product.getId());
        } catch (IllegalArgumentException exception) {
            return ActionResult.failure(messages.get("product.error.validation") + " " + exception.getMessage());
        }
    }

    public ActionResult removeProductByParameter(String param) {
        Product removed = removeByParam(param);
        if (removed == null) {
            return ActionResult.failure(messages.get("product.remove.notFound"));
        }
        String message = messages.get("product.remove.success") + " id=" + removed.getId() + " | " + removed.getDescription();
        return ActionResult.success(message);
    }

    public List<Product> getProducts() {
        return productModel.getProducts();
    }

    public ActionResult exportProducts(File file) {
        List<Product> products = productModel.getProducts();
        if (products.isEmpty()) {
            return ActionResult.failure(messages.get("product.export.empty"));
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("id,descripcion,unidad,cantidad,precio");
            writer.newLine();
            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                writer.write(product.getId() + "," + escapeCsv(product.getDescription()) + "," + escapeCsv(product.getUnit())
                        + "," + product.getQuantity().toPlainString() + "," + product.getPrice().toPlainString());
                writer.newLine();
            }
            return ActionResult.success(messages.get("product.export.success") + " " + file.getAbsolutePath());
        } catch (IOException exception) {
            return ActionResult.failure(messages.get("product.export.error") + " " + exception.getMessage());
        } finally {
            closeQuietly(writer);
        }
    }

    private Product removeByParam(String param) {
        try {
            return productModel.removeProductById(Integer.parseInt(param));
        } catch (NumberFormatException exception) {
            return productModel.removeProductByName(param);
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private void closeQuietly(BufferedWriter writer) {
        if (writer == null) {
            return;
        }
        try {
            writer.close();
        } catch (IOException ignored) {
        }
    }
}
