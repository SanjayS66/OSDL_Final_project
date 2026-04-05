package models;
import java.io.Serializable;

public abstract class Room implements Serializable {
    private int roomNumber;
    private RoomType roomType;
    private boolean isAvailable;

    public Room(int roomNumber, RoomType roomType) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.isAvailable = true;
    }

    public int getRoomNumber() { return roomNumber; }
    public RoomType getRoomType() { return roomType; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { this.isAvailable = available; }
    public String getGuestName() { return null; }

    // Abstract method to be overridden by subclasses
    public abstract double calculateTariff(int daysStayed);
    
    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + roomType + ") - " + (isAvailable ? "Available" : "Booked");
    }
}