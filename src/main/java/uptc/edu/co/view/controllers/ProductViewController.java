package uptc.edu.co.view.controllers;

import java.io.File;
import java.math.BigDecimal;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import uptc.edu.co.i18n.MessageService;
import uptc.edu.co.pojo.Product;
import uptc.edu.co.presenter.ActionResult;
import uptc.edu.co.presenter.ProductPresenter;
import uptc.edu.co.structures.DoubleList;

public class ProductViewController extends AbstractViewController {
    private final ProductPresenter presenter;

    public ProductViewController(JFrame parent, MessageService messages, ProductPresenter presenter) {
        super(parent, messages);
        this.presenter = presenter;
    }

    public void addProduct() {
        ProductFormData data = readProductFormData();
        if (data == null || data.price == null || data.quantity == null) {
            return;
        }
        ActionResult result = presenter.registerProduct(data.description, data.unit, data.quantity, data.price);
        showMessage(result.getMessage());
    }

    public void removeProductByParameter() {
        String param = readInput("product.remove.prompt.parameter");
        if (isBlank(param)) {
            return;
        }
        ActionResult result = presenter.removeProductByParameter(param.trim());
        showMessage(result.getMessage());
    }

    public void openProductListWindow() {
        JFrame frame = createListFrame("menu.product.list");
        DefaultTableModel model = nonEditableModel(new Object[] { messages.get("product.list.col.id"),
                messages.get("product.list.col.description"), messages.get("product.list.col.unit"),
                messages.get("product.list.col.quantity"), messages.get("product.list.col.price") });
        addProductRows(model, presenter.getProducts());
        JTable table = createAlignedTable(model);
        showListWindow(frame, table, null);
    }

    public void exportProductsToCsv() {
        DoubleList<Product> products = presenter.getProducts();
        if (products.isEmpty()) {
            showMessage(messages.get("product.export.empty"));
            return;
        }
        File file = chooseExportFile("productos.csv");
        if (file != null) {
            showMessage(presenter.exportProducts(file).getMessage());
        }
    }

    private ProductFormData readProductFormData() {
        JTextField descriptionField = new JTextField();
        JComboBox<String> unitCombo = createUnitCombo();
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();
        javax.swing.JPanel form = createFormPanel();
        buildProductForm(form, descriptionField, unitCombo, quantityField, priceField);
        if (!confirmForm(form, "menu.product.add")) {
            return null;
        }
        return buildProductData(descriptionField, unitCombo, quantityField, priceField);
    }

    private JComboBox<String> createUnitCombo() {
        return new JComboBox<String>(new String[] { messages.get("option.unit.libra"), messages.get("option.unit.kilos"),
                messages.get("option.unit.bultos"), messages.get("option.unit.toneladas") });
    }

    private void buildProductForm(javax.swing.JPanel form, JTextField descriptionField, JComboBox<String> unitCombo,
            JTextField quantityField, JTextField priceField) {
        addFormRow(form, "product.prompt.description", descriptionField);
        addFormRow(form, "product.prompt.unit", unitCombo);
        addFormRow(form, "product.prompt.quantity", quantityField);
        addFormRow(form, "product.prompt.price", priceField);
    }

    private ProductFormData buildProductData(JTextField descriptionField, JComboBox<String> unitCombo,
            JTextField quantityField, JTextField priceField) {
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

    private void addProductRows(DefaultTableModel model, DoubleList<Product> products) {
        for (int index = 0; index < products.size(); index++) {
            Product product = products.get(index);
            model.addRow(new Object[] { Integer.valueOf(product.getId()), product.getDescription(),
                    localizeUnit(product.getUnit()), product.getQuantity().toPlainString(),
                    product.getPrice().toPlainString() });
        }
    }

    private String toCanonicalUnit(String selectedLabel) {
        String canonical = mapLocalizedUnit(selectedLabel);
        if (canonical != null) {
            return canonical;
        }
        return safeTrim(selectedLabel).toLowerCase();
    }

    private String mapLocalizedUnit(String selectedLabel) {
        String[] labels = new String[] { messages.get("option.unit.libra"), messages.get("option.unit.kilos"),
                messages.get("option.unit.bultos"), messages.get("option.unit.toneladas") };
        String[] canonicalUnits = new String[] { "libra", "kilos", "bultos", "toneladas" };
        return firstMappedValue(selectedLabel, labels, canonicalUnits, false);
    }

    private String localizeUnit(String canonicalUnit) {
        String localized = mapCanonicalUnit(canonicalUnit);
        return localized == null ? canonicalUnit : localized;
    }

    private String mapCanonicalUnit(String canonicalUnit) {
        String[] canonicalUnits = new String[] { "libra", "kilos", "bultos", "toneladas" };
        String[] labels = new String[] { messages.get("option.unit.libra"), messages.get("option.unit.kilos"),
                messages.get("option.unit.bultos"), messages.get("option.unit.toneladas") };
        return firstMappedValue(canonicalUnit, canonicalUnits, labels, true);
    }

    private String firstMappedValue(String value, String[] keys, String[] values, boolean ignoreCase) {
        for (int index = 0; index < keys.length; index++) {
            if (matches(value, keys[index], ignoreCase)) {
                return values[index];
            }
        }
        return null;
    }

    private boolean matches(String value, String key, boolean ignoreCase) {
        if (ignoreCase) {
            return key.equalsIgnoreCase(value);
        }
        return key.equals(value);
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
