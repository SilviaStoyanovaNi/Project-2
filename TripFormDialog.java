import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class TripFormDialog extends JDialog {
        private JTextField nameField, countryField, cityField, descriptionField;
        private JTextField startDateField, endDateField, priceField, slotsField;

        private TravelManager manager;
        private Trip editingTrip;

        public TripFormDialog(JFrame parent, TravelManager manager, Trip tripToEdit) {
            super(parent, "Offer", true);
            this.manager = manager;
            this.editingTrip
                    = tripToEdit;

            setSize(400, 400);
            setLocationRelativeTo(parent);
            setLayout(new GridLayout(10, 2, 5, 5));

            nameField = new JTextField();
            countryField = new JTextField();
            cityField = new JTextField();
            descriptionField = new JTextField();
            startDateField = new JTextField("2025-06-01");
            endDateField = new JTextField("2025-06-10");
            priceField = new JTextField("1000");
            slotsField = new JTextField("10");

            add(new JLabel("Name:")); add(nameField);
            add(new JLabel("Country:")); add(countryField);
            add(new JLabel("City:")); add(cityField);
            add(new JLabel("Description:")); add(descriptionField);
            add(new JLabel("Departure (yyyy-MM-dd):")); add(startDateField);
            add(new JLabel("Return (yyyy-MM-dd):")); add(endDateField);
            add(new JLabel("Price:")); add(priceField);
            add(new JLabel("Free spots:")); add(slotsField);

            JButton btnSave = new JButton("Save");
            btnSave.addActionListener(e -> saveTrip());
            add(btnSave);

            JButton btnCancel = new JButton("Refuse");
            btnCancel.addActionListener(e -> dispose());
            add(btnCancel);

            if (editingTrip != null) {
                loadTripData();
            }
        }

        private void loadTripData() {
            nameField.setText(editingTrip.getName());
            countryField.setText(editingTrip.getDestination().getCountry());
            cityField.setText(editingTrip.getDestination().getCity());
            descriptionField.setText(editingTrip.getDestination().getDescription());
            startDateField.setText(editingTrip.getStartDate().toString());
            endDateField.setText(editingTrip.getEndDate().toString());
            priceField.setText(String.valueOf(editingTrip.getPrice()));
            slotsField.setText(String.valueOf(editingTrip.getAvailableSpots()));
        }

        private void saveTrip() {
            try {
                String name = nameField.getText().trim();
                String country = countryField.getText().trim();
                String city = cityField.getText().trim();
                String desc = descriptionField.getText().trim();
                LocalDate start = LocalDate.parse(startDateField.getText().trim());
                LocalDate end = LocalDate.parse(endDateField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                int slots = Integer.parseInt(slotsField.getText().trim());

                if (start.isAfter(end)) {
                    throw new IllegalArgumentException("Return can not be before departure.");
                }

                if (price <= 0 || slots < 0) {
                    throw new IllegalArgumentException("Price has to be positive and the spots >= 0.");
                }

                Destination dest = new Destination(country, city, desc);
                Trip newTrip = new Trip(name, dest, start, end, price, slots);

                if (editingTrip != null) {
                    manager.removeTrip(editingTrip);
                    manager.addTrip(newTrip);
                } else {
                    if (!manager.addTrip(newTrip)) {
                        JOptionPane.showMessageDialog(this, "This offer already exist.", "Duplication", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                manager.saveToFile("trips.txt");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Mistake: " + ex.getMessage(), "Invalid input", JOptionPane.ERROR_MESSAGE);
            }
        }
}
