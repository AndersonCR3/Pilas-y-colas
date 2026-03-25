package uptc.edu.co;

import javax.swing.SwingUtilities;

import uptc.edu.co.bootstrap.ApplicationBootstrap;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ApplicationBootstrap().start();
            }
        });
    }
}
