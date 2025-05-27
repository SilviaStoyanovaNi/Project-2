import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TravelApp extends JFrame {
    private TravelManager manager;
    private JTable tripTable;
    private DefaultTableModel tableModel;

    private JTextField searchCountryField, priceMinField, priceMaxField;
    private JButton btnSearch, btnAdd, btnEdit, btnDelete, btnSave, btnLoad, btnSortPrice, btnSortDate;

    public TravelApp(TravelManager manager) {
        this.manager = manager;
        this.manager.loadFromFile("trips.txt");
        setupUI();
        refreshTable(this.manager.getAllTrips());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                manager.saveToFile("trips.txt");
            }
        });
    }

    private void setupUI() {
        setTitle("Travel Agency");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 14));
        UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("TextField.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("Table.font", new Font("SansSerif", Font.PLAIN, 13));
        UIManager.put("TableHeader.font", new Font("SansSerif", Font.BOLD, 14));

        getContentPane().setBackground(new Color(245, 250, 255));

        String[] columns = {"Name", "Country", "City", "Departure", "Return", "Price", "Free spots"};
        tableModel = new DefaultTableModel(columns, 0);
        tripTable = new JTable(tableModel);
        tripTable.setRowHeight(24);
        tripTable.setGridColor(Color.LIGHT_GRAY);
        tripTable.setFillsViewportHeight(true);
        tripTable.setSelectionBackground(new Color(173, 216, 230));
        add(new JScrollPane(tripTable), BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(220, 235, 250));
        searchPanel.setBorder(BorderFactory.createTitledBorder("🔍 Search Offers"));

        searchCountryField = new JTextField(10);
        priceMinField = new JTextField(5);
        priceMaxField = new JTextField(5);

        btnSearch = new JButton("Search");
        btnSearch.setBackground(new Color(70, 130, 180));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> searchTrips());

        searchPanel.add(new JLabel("Country:"));
        searchPanel.add(searchCountryField);
        searchPanel.add(new JLabel("Price from:"));
        searchPanel.add(priceMinField);
        searchPanel.add(new JLabel("to:"));
        searchPanel.add(priceMaxField);
        searchPanel.add(btnSearch);

        add(searchPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        buttonPanel.setBackground(new Color(245, 250, 255));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnAdd = new JButton("Add");
        btnEdit = new JButton("Edit");
        btnDelete = new JButton("Delete");
        btnSave = new JButton("Save");
        btnLoad = new JButton("Load");
        btnSortPrice = new JButton("Sort by Price");
        btnSortDate = new JButton("Sort by Date");

        JButton[] buttons = {btnAdd, btnEdit, btnDelete, btnSave, btnLoad, btnSortPrice, btnSortDate};
        for (JButton b : buttons) {
            b.setBackground(new Color(100, 149, 237));
            b.setForeground(Color.WHITE);
        }

        btnAdd.addActionListener(e -> {
            openTripDialog(null);
            manager.saveToFile("trips.txt");
        });

        btnEdit.addActionListener(e -> {
            editSelectedTrip();
            manager.saveToFile("trips.txt");
        });

        btnDelete.addActionListener(e -> {
            deleteSelectedTrip();
            manager.saveToFile("trips.txt");
        });

        btnSave.addActionListener(e -> manager.saveToFile("trips.txt"));
        btnLoad.addActionListener(e -> {
            manager.loadFromFile("trips.txt");
            refreshTable(manager.getAllTrips());
        });

        btnSortPrice.addActionListener(e -> {
            manager.sortByPrice();
            refreshTable(manager.getAllTrips());
        });

        btnSortDate.addActionListener(e -> {
            manager.sortByStartDate();
            refreshTable(manager.getAllTrips());
        });

        for (JButton b : buttons) buttonPanel.add(b);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    private void refreshTable(MyList<Trip> trips) {
        tableModel.setRowCount(0);
        for (int i = 0; i < trips.size(); i++) {
            Trip t = trips.get(i);
            tableModel.addRow(new Object[]{
                    t.getName(),
                    t.getDestination().getCountry(),
                    t.getDestination().getCity(),
                    t.getStartDate(),
                    t.getEndDate(),
                    t.getPrice(),
                    t.getAvailableSpots()
            });
        }
    }

    private void searchTrips() {
        String country = searchCountryField.getText().trim();
        double min = 0, max = Double.MAX_VALUE;

        try {
            if (!priceMinField.getText().isEmpty())
                min = Double.parseDouble(priceMinField.getText());
            if (!priceMaxField.getText().isEmpty())
                max = Double.parseDouble(priceMaxField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price value.", "Mistake", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MyList<Trip> result = country.isEmpty() ?
                manager.searchByPriceRange(min, max) :
                manager.searchByCountry(country);

        refreshTable(result);
    }

    private void openTripDialog(Trip toEdit) {
        TripFormDialog dialog = new TripFormDialog(this, manager, toEdit);
        dialog.setVisible(true);
        refreshTable(manager.getAllTrips());
    }

    private void editSelectedTrip() {
        int row = tripTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an offer.", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = (String) tableModel.getValueAt(row, 0);
        for (int i = 0; i < manager.getAllTrips().size(); i++) {
            if (manager.getAllTrips().get(i).getName().equals(name)) {
                openTripDialog(manager.getAllTrips().get(i));
                break;
            }
        }
    }

    private void deleteSelectedTrip() {
        int row = tripTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an offer.", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = (String) tableModel.getValueAt(row, 0);
        for (int i = 0; i < manager.getAllTrips().size(); i++) {
            Trip t = manager.getAllTrips().get(i);
            if (t.getName().equals(name)) {
                manager.removeTrip(t);
                refreshTable(manager.getAllTrips());
                break;
            }
        }
    }
}