package models;

public class StandardRoom extends Room {
    public StandardRoom(int roomNumber) {
        super(roomNumber, RoomType.STANDARD);
    }

    @Override
    public double calculateTariff(int daysStayed) {
        return getRoomType().getBaseTariff() * daysStayed;
    }
}