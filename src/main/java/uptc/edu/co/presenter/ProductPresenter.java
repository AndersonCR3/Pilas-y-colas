package uptc.edu.co.presenter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uptc.edu.co.i18n.MessageService;
import uptc.edu.co.interfaces.IProductModel;
import uptc.edu.co.pojo.Product;
import uptc.edu.co.view.ProductView;

public class ProductPresenter {
    private static final Logger LOGGER = LogManager.getLogger(ProductPresenter.class);

    private final IProductModel productModel;
    private final ProductView productView;
    private final MessageService messages;

    public ProductPresenter(IProductModel productModel, ProductView productView, MessageService messages) {
        this.productModel = productModel;
        this.productView = productView;
        this.messages = messages;
    }

    public void registerProduct() {
        String description = productView.readText(messages.get("product.prompt.description"));
        String unit = productView.readText(messages.get("product.prompt.unit"));
        String priceText = productView.readText(messages.get("product.prompt.price"));

        BigDecimal price;
        try {
            price = new BigDecimal(priceText.trim());
        } catch (Exception exception) {
            productView.showMessage(messages.get("product.error.price"));
            return;
        }

        try {
            Product product = productModel.createProduct(description, unit, price);
            productView.showMessage(messages.get("product.success.created") + " " + product.getId());
            LOGGER.info("Producto registrado con id {}", Integer.valueOf(product.getId()));
        } catch (IllegalArgumentException exception) {
            productView.showMessage(messages.get("product.error.validation") + " " + exception.getMessage());
        }
    }

    public void listProducts() {
        List<Product> products = productModel.getProducts();
        if (products.isEmpty()) {
            productView.showMessage(messages.get("product.list.empty"));
            return;
        }

        productView.showMessage(messages.get("product.list.header"));
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            productView.showMessage((i + 1) + ". id=" + product.getId() + " | descripcion=" + product.getDescription()
                    + " | unidad=" + product.getUnit() + " | precio=" + product.getPrice().toPlainString());
        }
    }

    public void removeProductFromList() {
        String idText = productView.readText(messages.get("product.remove.prompt.id"));
        int id;
        try {
            id = Integer.parseInt(idText.trim());
        } catch (Exception exception) {
            productView.showMessage(messages.get("product.remove.invalidId"));
            return;
        }

        Product removed = productModel.removeProductById(id);
        if (removed == null) {
            productView.showMessage(messages.get("product.remove.notFound"));
            return;
        }

        productView.showMessage(messages.get("product.remove.success") + " id=" + removed.getId() + " | "
                + removed.getDescription());
        LOGGER.info("Producto retirado de la lista: id {}", Integer.valueOf(removed.getId()));
    }

    public void exportProductsToCsv() {
        List<Product> products = productModel.getProducts();
        if (products.isEmpty()) {
            productView.showMessage(messages.get("product.export.empty"));
            return;
        }

        String fileName = productView.readText(messages.get("product.export.fileName"));
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "productos.csv";
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileName.trim()));
            writer.write("id,descripcion,unidad,precio");
            writer.newLine();

            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                writer.write(product.getId() + "," + escapeCsv(product.getDescription()) + ","
                        + escapeCsv(product.getUnit()) + "," + product.getPrice().toPlainString());
                writer.newLine();
            }

            productView.showMessage(messages.get("product.export.success") + " " + fileName.trim());
            LOGGER.info("Productos exportados a CSV: {}", fileName.trim());
        } catch (IOException exception) {
            productView.showMessage(messages.get("product.export.error") + " " + exception.getMessage());
            LOGGER.error("Error exportando productos a CSV", exception);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                    LOGGER.warn("No se pudo cerrar el archivo CSV de productos.");
                }
            }
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
