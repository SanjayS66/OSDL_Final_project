package ui;

import models.*;
import services.HotelManager;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class Dashboard {
    private VBox view;
    private HotelManager manager;
    private TextArea displayArea; // To show logs and available rooms

    public Dashboard() {
        manager = new HotelManager(); // Connect to the backend
        buildUI();
    }

    public VBox getView() {
        return view;
    }

    private void buildUI() {
        view = new VBox(15);
        view.setPadding(new Insets(20));

        // --- 1. System Display Area ---
        displayArea = new TextArea();
        displayArea.setEditable(false);
        displayArea.setPrefHeight(200);
        refreshDisplay("System Initialized. Ready for operations.");

        // --- 2. Add Room Section (GridPane) ---
        GridPane roomGrid = new GridPane();
        roomGrid.setHgap(10); roomGrid.setVgap(10); 
        
        TextField txtRoomNumber = new TextField();
        ComboBox<RoomType> cbRoomType = new ComboBox<>();
        cbRoomType.getItems().addAll(RoomType.values()); // Populate enum values
        Button btnAddRoom = new Button("Add Room");

        roomGrid.add(new Label("Room No:"), 0, 0); 
        roomGrid.add(txtRoomNumber, 1, 0);
        roomGrid.add(new Label("Type:"), 2, 0);
        roomGrid.add(cbRoomType, 3, 0);
        roomGrid.add(btnAddRoom, 4, 0);

        // Event handling for adding a room [cite: 1587]
        btnAddRoom.setOnAction(e -> {
            try {
                int roomNo = Integer.parseInt(txtRoomNumber.getText());
                RoomType type = cbRoomType.getValue();
                if (type != null) {
                    Room newRoom = type == RoomType.DELUXE ? new DeluxeRoom(roomNo) : new StandardRoom(roomNo);
                    manager.addRoom(newRoom);
                    refreshDisplay("Room " + roomNo + " added successfully!");
                    txtRoomNumber.clear(); // Clear input fields after successful operations [cite: 1590]
                }
            } catch (NumberFormatException ex) {
                refreshDisplay("Error: Invalid Room Number.");
            }
        });

        // --- 3. Booking Section (GridPane) ---
        GridPane bookGrid = new GridPane();
        bookGrid.setHgap(10); bookGrid.setVgap(10);

        TextField txtCustId = new TextField();
        TextField txtCustName = new TextField();
        TextField txtCustPhone = new TextField();
        TextField txtBookRoomNo = new TextField();
        Button btnBook = new Button("Book Room");

        bookGrid.add(new Label("Cust ID:"), 0, 0); bookGrid.add(txtCustId, 1, 0);
        bookGrid.add(new Label("Name:"), 2, 0); bookGrid.add(txtCustName, 3, 0);
        bookGrid.add(new Label("Phone:"), 0, 1); bookGrid.add(txtCustPhone, 1, 1);
        bookGrid.add(new Label("Room No:"), 2, 1); bookGrid.add(txtBookRoomNo, 3, 1);
        bookGrid.add(btnBook, 1, 2);

        // Event handling for booking a room
        btnBook.setOnAction(e -> {
            try {
                int roomNo = Integer.parseInt(txtBookRoomNo.getText());
                Customer cust = new Customer(txtCustId.getText(), txtCustName.getText(), txtCustPhone.getText());
                
                boolean success = manager.bookRoom(roomNo, cust);
                if (success) {
                    refreshDisplay("Successfully booked Room " + roomNo + " for " + cust.getName());
                    txtCustId.clear(); txtCustName.clear(); txtCustPhone.clear(); txtBookRoomNo.clear();
                } else {
                    refreshDisplay("Error: Room " + roomNo + " is unavailable or does not exist."); 
                }
            } catch (NumberFormatException ex) {
                refreshDisplay("Error: Invalid Room Number format.");
            }
        });

        // --- 4. Checkout Section ---
        HBox checkoutBox = new HBox(10);
        TextField txtCheckoutRoomNo = new TextField();
        Button btnCheckout = new Button("Checkout");
        checkoutBox.getChildren().addAll(new Label("Room No for Checkout:"), txtCheckoutRoomNo, btnCheckout);

        btnCheckout.setOnAction(e -> {
            try {
                int roomNo = Integer.parseInt(txtCheckoutRoomNo.getText());
                if (manager.checkout(roomNo)) {
                    refreshDisplay("Room " + roomNo + " checked out successfully.");
                    txtCheckoutRoomNo.clear();
                } else {
                    refreshDisplay("Error: Room " + roomNo + " is not currently booked.");
                }
            } catch (NumberFormatException ex) {
                refreshDisplay("Error: Invalid Room Number.");
            }
        });

        // Assemble all sections vertically
        view.getChildren().addAll(
            new Label("--- HOTEL DASHBOARD ---"),
            displayArea,
            new Separator(),
            new Label("ADD NEW ROOM"), roomGrid,
            new Separator(),
            new Label("BOOK ROOM"), bookGrid,
            new Separator(),
            checkoutBox
        );
    }

    // Helper method to update the display area
    private void refreshDisplay(String message) {
        StringBuilder sb = new StringBuilder();
        sb.append(message).append("\n\n");
        sb.append("--- AVAILABLE ROOMS ---\n");
        for (Room r : manager.getAvailableRooms()) {
            sb.append(r.toString()).append("\n");
        }
        displayArea.setText(sb.toString());
    }
}