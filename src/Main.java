import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            view.MainMenuView mainMenu = new view.MainMenuView();
            mainMenu.setVisible(true);
        });
    }
}
