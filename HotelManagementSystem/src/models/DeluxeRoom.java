package models;

public class DeluxeRoom extends Room {
    private boolean freeWifi = true;
    private boolean complimentaryBreakfast = true;

    public DeluxeRoom(int roomNumber) {
        super(roomNumber, RoomType.DELUXE);
    }

    @Override
    public double calculateTariff(int daysStayed) {
        // Deluxe adds a flat premium charge of 500
        return (getRoomType().getBaseTariff() * daysStayed) + 500.0;
    }
}