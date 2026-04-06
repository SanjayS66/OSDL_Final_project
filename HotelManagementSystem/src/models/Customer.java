package models;
import java.io.Serializable;

public class Customer implements Serializable {
    private String customerId;
    private String name;
    private String contactNumber;
    private int allocatedRoomNumber;

    public Customer(String customerId, String name, String contactNumber) {
        this.customerId = customerId;
        this.name = name;
        this.contactNumber = contactNumber;
        this.allocatedRoomNumber = -1; 
    }

    public String getName() { return name; }
    public int getAllocatedRoomNumber() { return allocatedRoomNumber; }
    public void setAllocatedRoomNumber(int allocatedRoomNumber) { this.allocatedRoomNumber = allocatedRoomNumber; }
    
    @Override
    public String toString() {
        return name + " (ID: " + customerId + ", Contact: " + contactNumber + ") - Room: " + allocatedRoomNumber;
    }
}