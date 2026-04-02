package models;

public enum RoomType {
    STANDARD(2000.0),
    DELUXE(3500.0),
    SUITE(5000.0);

    private final double baseTariff;

    RoomType(double baseTariff) {
        this.baseTariff = baseTariff;
    }

    public double getBaseTariff() {
        return baseTariff;
    }
}