package uptc.edu.co.view;

public interface AccountingView {
    void showAccountingMenu();

    int readOption();

    String readText(String prompt);

    void showMessage(String message);
}
