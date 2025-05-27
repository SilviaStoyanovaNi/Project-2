import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TravelManager manager = new TravelManager();
            manager.saveToFile("trips.txt");
            manager.loadFromFile("trips.txt");
            TravelApp app = new TravelApp(manager);
            app.setVisible(true);
        });
    }
}
