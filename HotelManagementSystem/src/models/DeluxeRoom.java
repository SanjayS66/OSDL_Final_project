package models;

public class DeluxeRoom extends Room {
    private boolean freeWifi = true;
    private boolean complimentaryBreakfast = true;

    public DeluxeRoom(int roomNumber) {
        super(roomNumber, RoomType.DELUXE);
    }

    @Override
    public double calculateTariff(int daysStayed) {
        double premium = (freeWifi ? 200.0 : 0.0) + (complimentaryBreakfast ? 300.0 : 0.0);
        return (getRoomType().getBaseTariff() * daysStayed) + premium;
    }
}