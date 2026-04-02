package services;
import models.*;
import java.util.*;

public class HotelManager {
    private ArrayList<Room> rooms;
    private HashMap<Integer, Customer> roomToCustomerMap;

    @SuppressWarnings("unchecked")
    public HotelManager() {
        // Attempt to load existing data from file
        Object[] savedData = (Object[]) FileManager.loadData();
        if (savedData != null) {
            rooms = (ArrayList<Room>) savedData[0];
            roomToCustomerMap = (HashMap<Integer, Customer>) savedData[1];
        } else {
            rooms = new ArrayList<>();
            roomToCustomerMap = new HashMap<>();
        }
    }

    public void addRoom(Room room) {
        rooms.add(room);
        saveState();
    }

    public List<Room> getAvailableRooms() {
        List<Room> available = new ArrayList<>();
        for (Room r : rooms) {
            if (r.isAvailable()) available.add(r);
        }
        return available;
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
        Object[] dataToSave = {rooms, roomToCustomerMap};
        FileManager.saveData(dataToSave);
    }
}