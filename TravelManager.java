import java.io.*;
import java.time.LocalDate;

public class TravelManager {
    private MyList<Trip> trips;
    private MyHashMap<String, MyList<Trip>> tripsByCountry;

    public TravelManager() {
        trips = new MyList<>();
        tripsByCountry = new MyHashMap<>();
    }

    public boolean addTrip(Trip trip) {
        if (trip.getEndDate().isBefore(trip.getStartDate())) {
            System.out.println("Error: Arrival date is before departure.");
            return false;
        }
        if (trip.getAvailableSpots() < 0) {
            System.out.println("Error: Free spots have to be above zero.");
            return false;
        }
        if (trips.contains(trip)) {
            System.out.println("Error: Offer already exists.");
            return false;
        }

        trips.add(trip);
        String countryKey = trip.getDestination().getCountry().toLowerCase();

        MyList<Trip> list = tripsByCountry.get(countryKey);
        if (list == null) {
            list = new MyList<>();
            tripsByCountry.put(countryKey, list);
        }
        list.add(trip);
        return true;
    }

    public boolean removeTrip(Trip trip) {
        boolean removed = trips.remove(trip);
        if (removed) {
            String key = trip.getDestination().getCountry().toLowerCase();
            MyList<Trip> list = tripsByCountry.get(key);
            if (list != null) list.remove(trip);
        }
        return removed;
    }

    public MyList<Trip> searchByCountry(String country) {
        MyList<Trip> result = tripsByCountry.get(country.toLowerCase());
        return result == null ? new MyList<>() : result;
    }

    public MyList<Trip> searchByCity(String city) {
        MyList<Trip> result = new MyList<>();
        for (int i = 0; i < trips.size(); i++) {
            if (trips.get(i).getDestination().getCity().equalsIgnoreCase(city)) {
                result.add(trips.get(i));
            }
        }
        return result;
    }

    public MyList<Trip> searchByPriceRange(double min, double max) {
        MyList<Trip> result = new MyList<>();
        for (int i = 0; i < trips.size(); i++) {
            double price = trips.get(i).getPrice();
            if (price >= min && price <= max) {
                result.add(trips.get(i));
            }
        }
        return result;
    }

    public MyList<Trip> searchByDatePeriod(LocalDate start, LocalDate end) {
        MyList<Trip> result = new MyList<>();
        for (int i = 0; i < trips.size(); i++) {
            Trip t = trips.get(i);
            if (!t.getStartDate().isAfter(end) && !t.getEndDate().isBefore(start)) {
                result.add(t);
            }
        }
        return result;
    }

    public void sortByPrice() {
        for (int i = 1; i < trips.size(); i++) {
            Trip key = trips.get(i);
            int j = i - 1;
            while (j >= 0 && trips.get(j).getPrice() > key.getPrice()) {
                trips.set(j + 1, trips.get(j));
                j--;
            }
            trips.set(j + 1, key);
        }
    }

    public void sortByStartDate() {
        for (int i = 1; i < trips.size(); i++) {
            Trip key = trips.get(i);
            int j = i - 1;
            while (j >= 0 && trips.get(j).getStartDate().isAfter(key.getStartDate())) {
                trips.set(j + 1, trips.get(j));
                j--;
            }
            trips.set(j + 1, key);
        }
    }

    public MyList<Trip> getAllTrips() {
        return trips;
    }

    public void clearAllTrips() {
        trips.clear();
        tripsByCountry = new MyHashMap<>();
    }

    public void saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int i = 0; i < trips.size(); i++) {
                Trip t = trips.get(i);
                writer.write(t.getName() + ";");
                writer.write(t.getDestination().getCountry() + ";");
                writer.write(t.getDestination().getCity() + ";");
                writer.write(t.getDestination().getDescription() + ";");
                writer.write(t.getStartDate().toString() + ";");
                writer.write(t.getEndDate().toString() + ";");
                writer.write(t.getPrice() + ";");
                writer.write(t.getAvailableSpots() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error with saving: " + e.getMessage());
        }
    }

    public void loadFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            clearAllTrips();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 8) {
                    String name = parts[0];
                    String country = parts[1];
                    String city = parts[2];
                    String desc = parts[3];
                    LocalDate start = LocalDate.parse(parts[4]);
                    LocalDate end = LocalDate.parse(parts[5]);
                    double price = Double.parseDouble(parts[6]);
                    int slots = Integer.parseInt(parts[7]);

                    Destination dest = new Destination(country, city, desc);
                    Trip trip = new Trip(name, dest, start, end, price, slots);
                    addTrip(trip);
                }
            }
        } catch (IOException e) {
            System.out.println("Error with reading the file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid data in the file: " + e.getMessage());
        }
    }
}
