package services;
import models.*;
import java.util.*;

public class HotelManager {
    private ArrayList<Room> rooms;
    private HashMap<Integer, Customer> roomToCustomerMap;
    private double standardBaseTariff;
    private double deluxeBaseTariff;

    @SuppressWarnings("unchecked")
    public HotelManager() {
        standardBaseTariff = RoomType.STANDARD.getBaseTariff();
        deluxeBaseTariff = RoomType.DELUXE.getBaseTariff();

        // Attempt to load existing data from file
        Object[] savedData = (Object[]) FileManager.loadData();
        if (savedData != null) {
            rooms = (ArrayList<Room>) savedData[0];
            roomToCustomerMap = (HashMap<Integer, Customer>) savedData[1];

            if (savedData.length > 2 && savedData[2] instanceof Map<?, ?> rawTariffMap) {
                Object std = rawTariffMap.get("STANDARD");
                Object del = rawTariffMap.get("DELUXE");

                if (std instanceof Number stdNum && stdNum.doubleValue() > 0) {
                    standardBaseTariff = stdNum.doubleValue();
                }
                if (del instanceof Number delNum && delNum.doubleValue() > 0) {
                    deluxeBaseTariff = delNum.doubleValue();
                }
            }
        } else {
            rooms = new ArrayList<>();
            roomToCustomerMap = new HashMap<>();
        }
    }

    public synchronized boolean addRoom(Room room) {
        if (room == null) {
            return false;
        }
        if (getRoomByNumber(room.getRoomNumber()) != null) {
            return false;
        }
        rooms.add(room);
        saveState();
        return true;
    }

    public List<Room> getAvailableRooms() {
        List<Room> available = new ArrayList<>();
        for (Room r : rooms) {
            if (r.isAvailable()) available.add(r);
        }
        return available;
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    public Room getRoomByNumber(int roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }

    public Customer getBookedCustomer(int roomNumber) {
        return roomToCustomerMap.get(roomNumber);
    }

    public int getTotalRooms() {
        return rooms.size();
    }

    public double getStandardBaseTariff() {
        return standardBaseTariff;
    }

    public double getDeluxeBaseTariff() {
        return deluxeBaseTariff;
    }

    public boolean updateBaseTariffs(double standardTariff, double deluxeTariff) {
        if (standardTariff <= 0 || deluxeTariff <= 0) {
            return false;
        }
        standardBaseTariff = standardTariff;
        deluxeBaseTariff = deluxeTariff;
        saveState();
        return true;
    }

    public double calculateBillAmount(Room room, int daysStayed) {
        if (room == null || daysStayed <= 0) {
            return 0.0;
        }

        double configuredBase = switch (room.getRoomType()) {
            case STANDARD -> standardBaseTariff;
            case DELUXE -> deluxeBaseTariff;
            default -> room.getRoomType().getBaseTariff();
        };

        // Preserve any room-specific surcharge logic while replacing the base tariff.
        double surcharge = room.calculateTariff(1) - room.getRoomType().getBaseTariff();
        return (configuredBase * daysStayed) + surcharge;
    }

    // Synchronized method to prevent double booking in multithreaded environments
    public synchronized boolean bookRoom(int roomNumber, Customer customer) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomNumber && r.isAvailable()) {
                r.setAvailable(false);
                customer.setAllocatedRoomNumber(roomNumber);
                roomToCustomerMap.put(roomNumber, customer);
                saveState();
                return true; // Booking successful
            }
        }
        return false; // Room unavailable or not found
    }

    public synchronized boolean checkout(int roomNumber) {
        if (roomToCustomerMap.containsKey(roomNumber)) {
            roomToCustomerMap.remove(roomNumber);
            for (Room r : rooms) {
                if (r.getRoomNumber() == roomNumber) {
                    r.setAvailable(true);
                    break;
                }
            }
            saveState();
            return true;
        }
        return false;
    }

    private void saveState() {
        Map<String, Double> tariffConfig = new HashMap<>();
        tariffConfig.put("STANDARD", standardBaseTariff);
        tariffConfig.put("DELUXE", deluxeBaseTariff);

        Object[] dataToSave = {rooms, roomToCustomerMap, tariffConfig};
        FileManager.saveData(dataToSave);
    }
}