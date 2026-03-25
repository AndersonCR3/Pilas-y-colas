package uptc.edu.co.view.controllers;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import uptc.edu.co.i18n.MessageService;
import uptc.edu.co.mediator.GuiMenuMediator;
import uptc.edu.co.pojo.Product;
import uptc.edu.co.presenter.ActionResult;

public class ProductViewController extends AbstractViewController {
    private final GuiMenuMediator menuMediator;

    public ProductViewController(JFrame parent, MessageService messages, GuiMenuMediator menuMediator) {
        super(parent, messages);
        this.menuMediator = menuMediator;
    }

    public void addProduct() {
        ProductFormData data = readProductFormData();
        if (data == null || data.price == null || data.quantity == null) {
            return;
        }
        ActionResult result = menuMediator.registerProduct(data.description, data.unit, data.quantity, data.price);
        showMessage(result.getMessage());
    }

    public void removeProductByParameter() {
        String param = readInput("product.remove.prompt.parameter");
        if (isBlank(param)) {
            return;
        }
        ActionResult result = menuMediator.removeProductByParameter(param.trim());
        showMessage(result.getMessage());
    }

    public void openProductListWindow() {
        JFrame frame = createListFrame("menu.product.list");
        DefaultTableModel model = nonEditableModel(new Object[] { messages.get("product.list.col.id"),
                messages.get("product.list.col.description"), messages.get("product.list.col.unit"),
                messages.get("product.list.col.quantity"), messages.get("product.list.col.price") });
        addProductRows(model, menuMediator.getProducts());
        JTable table = createAlignedTable(model);
        showListWindow(frame, table, null);
    }

    public void exportProductsToCsv() {
        List<Product> products = menuMediator.getProducts();
        if (products.isEmpty()) {
            showMessage(messages.get("product.export.empty"));
            return;
        }
        File file = chooseExportFile("productos.csv");
        if (file != null) {
            showMessage(menuMediator.exportProducts(file).getMessage());
        }
    }

    private ProductFormData readProductFormData() {
        JTextField descriptionField = new JTextField();
        JComboBox<String> unitCombo = new JComboBox<String>(new String[] { messages.get("option.unit.libra"),
            messages.get("option.unit.kilos"), messages.get("option.unit.bultos"),
            messages.get("option.unit.toneladas") });
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();
        javax.swing.JPanel form = createFormPanel();
        addFormRow(form, "product.prompt.description", descriptionField);
        addFormRow(form, "product.prompt.unit", unitCombo);
        addFormRow(form, "product.prompt.quantity", quantityField);
        addFormRow(form, "product.prompt.price", priceField);
        if (!confirmForm(form, "menu.product.add")) {
            return null;
        }
        BigDecimal quantity = parseDecimal(quantityField.getText(), "product.error.quantity");
        BigDecimal price = parseDecimal(priceField.getText(), "product.error.price");
        String canonicalUnit = toCanonicalUnit(String.valueOf(unitCombo.getSelectedItem()));
        return new ProductFormData(safeTrim(descriptionField.getText()), canonicalUnit, quantity, price);
    }

    private BigDecimal parseDecimal(String raw, String errorKey) {
        try {
            return new BigDecimal(safeTrim(raw));
        } catch (Exception exception) {
            showMessage(messages.get(errorKey));
            return null;
        }
    }

    private void addProductRows(DefaultTableModel model, List<Product> products) {
        for (int index = 0; index < products.size(); index++) {
            Product product = products.get(index);
            model.addRow(new Object[] { Integer.valueOf(product.getId()), product.getDescription(),
                    localizeUnit(product.getUnit()), product.getQuantity().toPlainString(),
                    product.getPrice().toPlainString() });
        }
    }

    private String toCanonicalUnit(String selectedLabel) {
        if (messages.get("option.unit.libra").equals(selectedLabel)) {
            return "libra";
        }
        if (messages.get("option.unit.kilos").equals(selectedLabel)) {
            return "kilos";
        }
        if (messages.get("option.unit.bultos").equals(selectedLabel)) {
            return "bultos";
        }
        if (messages.get("option.unit.toneladas").equals(selectedLabel)) {
            return "toneladas";
        }
        return safeTrim(selectedLabel).toLowerCase();
    }

    private String localizeUnit(String canonicalUnit) {
        if ("libra".equalsIgnoreCase(canonicalUnit)) {
            return messages.get("option.unit.libra");
        }
        if ("kilos".equalsIgnoreCase(canonicalUnit)) {
            return messages.get("option.unit.kilos");
        }
        if ("bultos".equalsIgnoreCase(canonicalUnit)) {
            return messages.get("option.unit.bultos");
        }
        if ("toneladas".equalsIgnoreCase(canonicalUnit)) {
            return messages.get("option.unit.toneladas");
        }
        return canonicalUnit;
    }

    private static final class ProductFormData {
        private final String description;
        private final String unit;
        private final BigDecimal quantity;
        private final BigDecimal price;

        private ProductFormData(String description, String unit, BigDecimal quantity, BigDecimal price) {
            this.description = description;
            this.unit = unit;
            this.quantity = quantity;
            this.price = price;
        }
    }
}
