package uptc.edu.co.view;

public interface PersonView {
    void showPersonMenu();

    int readOption();

    String readText(String prompt);

    void showMessage(String message);
}
