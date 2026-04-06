package services;
import models.Customer;

public class BookingTask implements Runnable {
    private HotelManager manager;
    private int roomNumber;
    private Customer customer;

    public BookingTask(HotelManager manager, int roomNumber, Customer customer) {
        this.manager = manager;
        this.roomNumber = roomNumber;
        this.customer = customer;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " attempting to book room " + roomNumber + "...");
        
        try {
            Thread.sleep(500); 
        } catch (InterruptedException e) {
            System.out.println("Booking interrupted.");
        }

        boolean success = manager.bookRoom(roomNumber, customer);
        if (success) {
            System.out.println(Thread.currentThread().getName() + " successfully booked Room " + roomNumber);
        } else {
            System.out.println(Thread.currentThread().getName() + " failed. Room " + roomNumber + " is unavailable.");
        }
    }
}