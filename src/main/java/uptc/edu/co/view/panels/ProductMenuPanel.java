package uptc.edu.co.view.panels;

import javax.swing.JButton;

import uptc.edu.co.i18n.MessageService;
import uptc.edu.co.view.components.MenuButtonFactory;
import uptc.edu.co.view.components.VerticalMenuPanel;

public class ProductMenuPanel extends VerticalMenuPanel {

    public ProductMenuPanel(MessageService messages, MenuButtonFactory buttonFactory, Runnable addProduct,
            Runnable removeProduct, Runnable listProducts, Runnable exportProducts, Runnable back) {
        super(messages.get("menu.product.title"),
                createButtons(messages, buttonFactory, addProduct, removeProduct, listProducts, exportProducts, back), 60);
    }

    private static JButton[] createButtons(MessageService messages, MenuButtonFactory buttonFactory, Runnable addProduct,
            Runnable removeProduct, Runnable listProducts, Runnable exportProducts, Runnable back) {
        return new JButton[] {
            buttonFactory.create(messages.get("menu.product.add"), addProduct),
            buttonFactory.create(messages.get("menu.product.remove"), removeProduct),
            buttonFactory.create(messages.get("menu.product.list"), listProducts),
            buttonFactory.create(messages.get("menu.product.export"), exportProducts),
            buttonFactory.create(messages.get("menu.product.back"), back)
        };
    }
}
