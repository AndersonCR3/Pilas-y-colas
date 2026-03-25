package uptc.edu.co.view;

public interface ProductView {
    void showProductMenu();

    int readOption();

    String readText(String prompt);

    void showMessage(String message);
}
