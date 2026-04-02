package models;

public class BookingPair<T, U> {
    private T roomIdentifier;
    private U guestDetail;

    public BookingPair(T roomIdentifier, U guestDetail) {
        this.roomIdentifier = roomIdentifier;
        this.guestDetail = guestDetail;
    }

    public T getRoomIdentifier() { return roomIdentifier; }
    public U getGuestDetail() { return guestDetail; }
}