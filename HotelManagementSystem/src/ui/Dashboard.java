package ui;

import models.*;
import services.HotelManager;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Redesigned Dashboard with:
 *  - Sidebar navigation (Dashboard / Add Room / Book Room / Checkout / All Rooms)
 *  - Animated page transitions (fade + translate)
 *  - Live stats cards on home page
 *  - Consistent dark-navy + amber accent design language
 */
public class Dashboard {

    // ── palette ────────────────────────────────────────────────────────────────
    private static final String C_BG        = "#0d1117";   // page background
    private static final String C_SIDEBAR    = "#161b22";   // sidebar
    private static final String C_CARD       = "#1c2333";   // card surface
    private static final String C_CARD_HOVER = "#21293a";
    private static final String C_BORDER     = "#30363d";
    private static final String C_ACCENT     = "#f0a500";   // amber gold
    private static final String C_ACCENT2    = "#e06c00";   // deep amber
    private static final String C_TEXT       = "#e6edf3";
    private static final String C_MUTED      = "#8b949e";
    private static final String C_GREEN      = "#3fb950";
    private static final String C_RED        = "#f85149";
    private static final String C_BLUE       = "#58a6ff";

    // ── state ──────────────────────────────────────────────────────────────────
    private HotelManager manager;
    private BorderPane rootView;
    private StackPane contentArea;
    private String activePage = "home";

    // sidebar nav buttons kept for highlight toggling
    private Button navHome, navAddRoom, navBookRoom, navCheckout, navAllRooms;

    // ── public entry point ─────────────────────────────────────────────────────
    public Dashboard() {
        manager = new HotelManager();
        buildRoot();
    }

    public BorderPane getView() { return rootView; }

    // ══════════════════════════════════════════════════════════════════════════
    //  ROOT LAYOUT
    // ══════════════════════════════════════════════════════════════════════════
    private void buildRoot() {
        rootView = new BorderPane();
        rootView.setStyle("-fx-background-color:" + C_BG + ";");

        rootView.setLeft(buildSidebar());

        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color:" + C_BG + ";");
        rootView.setCenter(contentArea);

        navigateTo("home");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SIDEBAR
    // ══════════════════════════════════════════════════════════════════════════
    private VBox buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.setPrefWidth(220);
        sidebar.setMinWidth(220);
        sidebar.setStyle("-fx-background-color:" + C_SIDEBAR
                + "; -fx-border-color:" + C_BORDER
                + "; -fx-border-width: 0 1 0 0;");

        // ── branding ──────────────────────────────────────────────────────────
        VBox brand = new VBox(4);
        brand.setPadding(new Insets(28, 20, 24, 20));

        Label hotelIcon = new Label("◈");
        hotelIcon.setFont(Font.font("Georgia", FontWeight.BOLD, 26));
        hotelIcon.setTextFill(Color.web(C_ACCENT));

        Label brandName = new Label("GRAND VISTA");
        brandName.setFont(Font.font("Georgia", FontWeight.BOLD, 15));
        brandName.setTextFill(Color.web(C_TEXT));

        Label brandSub = new Label("Hotel Management");
        brandSub.setFont(Font.font("Georgia", FontWeight.LIGHT, 10));
        brandSub.setTextFill(Color.web(C_MUTED));

        brand.getChildren().addAll(hotelIcon, brandName, brandSub);

        // ── divider ───────────────────────────────────────────────────────────
        Separator sep1 = styledSeparator();

        // ── section label ─────────────────────────────────────────────────────
        Label navLabel = new Label("NAVIGATION");
        navLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 9));
        navLabel.setTextFill(Color.web(C_MUTED));
        navLabel.setPadding(new Insets(12, 20, 6, 20));

        // ── nav buttons ───────────────────────────────────────────────────────
        navHome     = navBtn("⌂", "Dashboard",   "home");
        navAddRoom  = navBtn("+", "Add Room",     "addRoom");
        navBookRoom = navBtn("⊞", "Book Room",    "bookRoom");
        navCheckout = navBtn("⊟", "Checkout",     "checkout");
        navAllRooms = navBtn("≡", "All Rooms",    "allRooms");

        // ── spacer + footer ───────────────────────────────────────────────────
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Separator sep2 = styledSeparator();

        Label footer = new Label("OOSD Week 10 Project");
        footer.setFont(Font.font("Courier New", 9));
        footer.setTextFill(Color.web(C_MUTED));
        footer.setPadding(new Insets(12, 20, 20, 20));

        sidebar.getChildren().addAll(brand, sep1, navLabel,
                navHome, navAddRoom, navBookRoom, navCheckout, navAllRooms,
                spacer, sep2, footer);
        return sidebar;
    }

    private Button navBtn(String icon, String label, String pageId) {
        HBox inner = new HBox(12);
        inner.setAlignment(Pos.CENTER_LEFT);

        Label ic = new Label(icon);
        ic.setFont(Font.font("Georgia", FontWeight.BOLD, 14));
        ic.setMinWidth(20);

        Label lbl = new Label(label);
        lbl.setFont(Font.font("Courier New", FontWeight.NORMAL, 13));

        inner.getChildren().addAll(ic, lbl);

        Button btn = new Button();
        btn.setGraphic(inner);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(11, 20, 11, 20));
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setStyle(navBtnStyle(false));

        btn.setOnMouseEntered(e -> {
            if (!activePage.equals(pageId))
                btn.setStyle(navBtnHoverStyle());
        });
        btn.setOnMouseExited(e -> {
            if (!activePage.equals(pageId))
                btn.setStyle(navBtnStyle(false));
            else
                btn.setStyle(navBtnStyle(true));
        });
        btn.setOnAction(e -> navigateTo(pageId));

        return btn;
    }

    private String navBtnStyle(boolean active) {
        if (active) {
            return "-fx-background-color:" + C_ACCENT + "22" + ";"
                    + "-fx-text-fill:" + C_ACCENT + ";"
                    + "-fx-border-color:" + C_ACCENT + " transparent transparent transparent;"
                    + "-fx-border-width: 0 0 0 3;"
                    + "-fx-background-radius:0; -fx-border-radius:0;";
        }
        return "-fx-background-color:transparent; -fx-text-fill:" + C_TEXT + ";"
                + "-fx-background-radius:0; -fx-border-radius:0;";
    }

    private String navBtnHoverStyle() {
        return "-fx-background-color:" + C_CARD + "; -fx-text-fill:" + C_TEXT + ";"
                + "-fx-background-radius:0; -fx-border-radius:0;";
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  NAVIGATION / TRANSITIONS
    // ══════════════════════════════════════════════════════════════════════════
    private void navigateTo(String pageId) {
        activePage = pageId;
        highlightNav(pageId);

        javafx.scene.Node page = switch (pageId) {
            case "addRoom"  -> buildAddRoomPage();
            case "bookRoom" -> buildBookRoomPage();
            case "checkout" -> buildCheckoutPage();
            case "allRooms" -> buildAllRoomsPage();
            default         -> buildHomePage();
        };

        // fade + slide in
        page.setOpacity(0);
        page.setTranslateX(18);

        contentArea.getChildren().setAll(page);

        FadeTransition ft = new FadeTransition(Duration.millis(220), page);
        ft.setToValue(1);

        TranslateTransition tt = new TranslateTransition(Duration.millis(220), page);
        tt.setToX(0);
        tt.setInterpolator(Interpolator.EASE_OUT);

        new ParallelTransition(ft, tt).play();
    }

    private void highlightNav(String pageId) {
        Button[] btns = {navHome, navAddRoom, navBookRoom, navCheckout, navAllRooms};
        String[] ids  = {"home", "addRoom", "bookRoom", "checkout", "allRooms"};
        for (int i = 0; i < btns.length; i++) {
            btns[i].setStyle(navBtnStyle(ids[i].equals(pageId)));
            // recolour the icon label too
            HBox hb = (HBox) btns[i].getGraphic();
            Label ic = (Label) hb.getChildren().get(0);
            Label lbl = (Label) hb.getChildren().get(1);
            String col = ids[i].equals(pageId) ? C_ACCENT : C_TEXT;
            ic.setTextFill(Color.web(col));
            lbl.setTextFill(Color.web(col));
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PAGE — HOME / DASHBOARD
    // ══════════════════════════════════════════════════════════════════════════
    private javafx.scene.Node buildHomePage() {
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:" + C_BG
                + "; -fx-background:" + C_BG + ";");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox page = new VBox(28);
        page.setPadding(new Insets(36, 36, 36, 36));
        page.setStyle("-fx-background-color:" + C_BG + ";");

        // ── header row ────────────────────────────────────────────────────────
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Dashboard");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        title.setTextFill(Color.web(C_TEXT));

        Label subtitle = new Label("Live overview of hotel operations");
        subtitle.setFont(Font.font("Courier New", 12));
        subtitle.setTextFill(Color.web(C_MUTED));
        titleBox.getChildren().addAll(title, subtitle);

        Region hSpacer = new Region();
        HBox.setHgrow(hSpacer, Priority.ALWAYS);

        // live clock
        Label clock = new Label();
        clock.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
        clock.setTextFill(Color.web(C_MUTED));
        Timeline ticker = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            clock.setText(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm:ss")));
        }));
        ticker.setCycleCount(Animation.INDEFINITE);
        ticker.play();

        header.getChildren().addAll(titleBox, hSpacer, clock);

        // ── stat cards row ────────────────────────────────────────────────────
        int total     = manager.getTotalRooms();
        int available = manager.getAvailableRooms().size();
        int occupied  = total - available;

        HBox stats = new HBox(16);
        stats.getChildren().addAll(
                statCard("Total Rooms",   String.valueOf(total),     C_BLUE,  "⬡"),
                statCard("Available",     String.valueOf(available),  C_GREEN, "◉"),
                statCard("Occupied",      String.valueOf(occupied),   C_RED,   "◎"),
                statCard("Guests Today",  String.valueOf(occupied),   C_ACCENT,"★")
        );

        // ── quick-action cards ────────────────────────────────────────────────
        Label qaLabel = pageSection("Quick Actions");
        HBox qa = new HBox(16);
        qa.getChildren().addAll(
                quickActionCard("⊞", "Book a Room",
                        "Assign a guest to an available room", "bookRoom"),
                quickActionCard("⊟", "Checkout",
                        "Release a room and clear the record",  "checkout"),
                quickActionCard("+", "Add Room",
                        "Register a new room in the system",   "addRoom"),
                quickActionCard("≡", "View All Rooms",
                        "Browse every room and its status",    "allRooms")
        );

        // ── recent rooms preview ──────────────────────────────────────────────
        Label rrLabel = pageSection("Available Rooms Preview");
        VBox roomList = new VBox(8);
        var avail = manager.getAvailableRooms();
        if (avail.isEmpty()) {
            Label none = new Label("No rooms currently available.");
            none.setFont(Font.font("Courier New", 12));
            none.setTextFill(Color.web(C_MUTED));
            roomList.getChildren().add(none);
        } else {
            for (Room r : avail) {
                roomList.getChildren().add(roomRowCard(r));
            }
        }

        page.getChildren().addAll(header, stats, qaLabel, qa, rrLabel, roomList);
        scroll.setContent(page);
        return scroll;
    }

    private VBox statCard(String label, String value, String accent, String icon) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20, 24, 20, 24));
        card.setMinWidth(140);
        card.setStyle("-fx-background-color:" + C_CARD
                + "; -fx-background-radius:10;"
                + " -fx-border-color:" + C_BORDER
                + "; -fx-border-radius:10; -fx-border-width:1;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);
        Label ic = new Label(icon);
        ic.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        ic.setTextFill(Color.web(accent));
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Courier New", 11));
        lbl.setTextFill(Color.web(C_MUTED));
        top.getChildren().addAll(ic, lbl);

        Label val = new Label(value);
        val.setFont(Font.font("Georgia", FontWeight.BOLD, 36));
        val.setTextFill(Color.web(accent));

        card.getChildren().addAll(top, val);
        addHoverLift(card);
        return card;
    }

    private VBox quickActionCard(String icon, String title, String desc, String targetPage) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20, 20, 20, 20));
        card.setMinWidth(160);
        card.setStyle("-fx-background-color:" + C_CARD
                + "; -fx-background-radius:10;"
                + "-fx-border-color:" + C_BORDER
                + "; -fx-border-radius:10; -fx-border-width:1; -fx-cursor:hand;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label ic = new Label(icon);
        ic.setFont(Font.font("Georgia", FontWeight.BOLD, 24));
        ic.setTextFill(Color.web(C_ACCENT));

        Label t = new Label(title);
        t.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        t.setTextFill(Color.web(C_TEXT));

        Label d = new Label(desc);
        d.setFont(Font.font("Courier New", 11));
        d.setTextFill(Color.web(C_MUTED));
        d.setWrapText(true);

        card.getChildren().addAll(ic, t, d);
        card.setOnMouseClicked(e -> navigateTo(targetPage));
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color:" + C_CARD_HOVER
                        + "; -fx-background-radius:10;"
                        + "-fx-border-color:" + C_ACCENT
                        + "; -fx-border-radius:10; -fx-border-width:1; -fx-cursor:hand;"));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color:" + C_CARD
                        + "; -fx-background-radius:10;"
                        + "-fx-border-color:" + C_BORDER
                        + "; -fx-border-radius:10; -fx-border-width:1; -fx-cursor:hand;"));
        return card;
    }

    private HBox roomRowCard(Room r) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle("-fx-background-color:" + C_CARD
                + "; -fx-background-radius:8;"
                + "-fx-border-color:" + C_BORDER
                + "; -fx-border-radius:8; -fx-border-width:1;");

        Label roomNum = new Label("#" + r.getRoomNumber());
        roomNum.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        roomNum.setTextFill(Color.web(C_ACCENT));
        roomNum.setMinWidth(50);

        Label type = new Label(r.getRoomType().toString());
        type.setFont(Font.font("Courier New", 12));
        type.setTextFill(Color.web(C_TEXT));

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label badge = new Label("● Available");
        badge.setFont(Font.font("Courier New", FontWeight.BOLD, 11));
        badge.setTextFill(Color.web(C_GREEN));

        row.getChildren().addAll(roomNum, type, sp, badge);
        return row;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PAGE — ADD ROOM
    // ══════════════════════════════════════════════════════════════════════════
    private javafx.scene.Node buildAddRoomPage() {
        ScrollPane page = centeredFormPage("Add New Room", "Register a new room in the hotel system");
        VBox outer = (VBox) page.getContent();

        VBox form = formCard();

        TextField txtRoomNumber = styledField("Room Number  (e.g. 205)");
        ComboBox<RoomType> cbType = new ComboBox<>();
        cbType.getItems().addAll(RoomType.values());
        cbType.setPromptText("Select room type…");
        styleCombo(cbType);

        Label feedback = feedbackLabel();
        Button btnAdd = primaryBtn("Add Room");

        btnAdd.setOnAction(e -> {
            String numTxt = txtRoomNumber.getText().trim();
            RoomType type = cbType.getValue();

            if (numTxt.isEmpty() || type == null) {
                feedbackError(feedback, "⚠  Please fill in all fields.");
                return;
            }
            try {
                int roomNo = Integer.parseInt(numTxt);
                Room newRoom = (type == RoomType.DELUXE)
                        ? new DeluxeRoom(roomNo) : new StandardRoom(roomNo);
                manager.addRoom(newRoom);
                feedbackOk(feedback, "✔  Room " + roomNo + " added successfully.");
                txtRoomNumber.clear();
                cbType.setValue(null);
            } catch (NumberFormatException ex) {
                feedbackError(feedback, "⚠  Room number must be a valid integer.");
            }
        });

        form.getChildren().addAll(
                formRow("Room Number", txtRoomNumber),
                formRow("Room Type",   cbType),
                feedback,
                btnAdd
        );

        outer.getChildren().add(form);
        return page;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PAGE — BOOK ROOM
    // ══════════════════════════════════════════════════════════════════════════
    private javafx.scene.Node buildBookRoomPage() {
        ScrollPane page = centeredFormPage("Book a Room", "Assign an available room to a guest");
        VBox outer = (VBox) page.getContent();

        VBox form = formCard();

        TextField txtCustId    = styledField("Customer ID  (e.g. C001)");
        TextField txtCustName  = styledField("Full Name");
        TextField txtCustPhone = styledField("Phone Number");
        TextField txtRoomNo    = styledField("Room Number to Book");

        Label feedback = feedbackLabel();
        Button btnBook = primaryBtn("Confirm Booking");

        btnBook.setOnAction(e -> {
            String id    = txtCustId.getText().trim();
            String name  = txtCustName.getText().trim();
            String phone = txtCustPhone.getText().trim();
            String roomS = txtRoomNo.getText().trim();

            if (id.isEmpty() || name.isEmpty() || phone.isEmpty() || roomS.isEmpty()) {
                feedbackError(feedback, "⚠  All fields are required.");
                return;
            }
            try {
                int roomNo = Integer.parseInt(roomS);
                Customer cust = new Customer(id, name, phone);
                boolean ok = manager.bookRoom(roomNo, cust);
                if (ok) {
                    feedbackOk(feedback, "✔  Room " + roomNo + " booked for " + name + ".");
                    txtCustId.clear(); txtCustName.clear();
                    txtCustPhone.clear(); txtRoomNo.clear();
                } else {
                    feedbackError(feedback, "⚠  Room " + roomNo + " is unavailable or does not exist.");
                }
            } catch (NumberFormatException ex) {
                feedbackError(feedback, "⚠  Room number must be a valid integer.");
            }
        });

        form.getChildren().addAll(
                formRow("Customer ID",   txtCustId),
                formRow("Full Name",     txtCustName),
                formRow("Phone Number",  txtCustPhone),
                formRow("Room Number",   txtRoomNo),
                feedback,
                btnBook
        );

        outer.getChildren().add(form);
        return page;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PAGE — CHECKOUT
    // ══════════════════════════════════════════════════════════════════════════
    private javafx.scene.Node buildCheckoutPage() {
        ScrollPane page = centeredFormPage("Checkout", "Release an occupied room");
        VBox outer = (VBox) page.getContent();

        VBox form = formCard();

        TextField txtRoomNo = styledField("Room Number to Checkout");
        Label feedback = feedbackLabel();
        Button btnCheckout = dangerBtn("Confirm Checkout");

        btnCheckout.setOnAction(e -> {
            String s = txtRoomNo.getText().trim();
            if (s.isEmpty()) {
                feedbackError(feedback, "⚠  Please enter a room number.");
                return;
            }
            try {
                int roomNo = Integer.parseInt(s);
                if (manager.checkout(roomNo)) {
                    feedbackOk(feedback, "✔  Room " + roomNo + " checked out successfully.");
                    txtRoomNo.clear();
                } else {
                    feedbackError(feedback, "⚠  Room " + roomNo + " is not currently booked.");
                }
            } catch (NumberFormatException ex) {
                feedbackError(feedback, "⚠  Room number must be a valid integer.");
            }
        });

        form.getChildren().addAll(
                formRow("Room Number", txtRoomNo),
                feedback,
                btnCheckout
        );

        outer.getChildren().add(form);
        return page;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PAGE — ALL ROOMS
    // ══════════════════════════════════════════════════════════════════════════
    private javafx.scene.Node buildAllRoomsPage() {
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:" + C_BG + "; -fx-background:" + C_BG + ";");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox page = new VBox(24);
        page.setPadding(new Insets(36));
        page.setStyle("-fx-background-color:" + C_BG + ";");

        // header
        VBox header = new VBox(4);
        Label title = new Label("All Rooms");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        title.setTextFill(Color.web(C_TEXT));
        Label sub = new Label("Full inventory of hotel rooms");
        sub.setFont(Font.font("Courier New", 12));
        sub.setTextFill(Color.web(C_MUTED));
        header.getChildren().addAll(title, sub);

        // table header
        HBox tableHeader = new HBox(0);
        tableHeader.setStyle("-fx-background-color:" + C_CARD
                + "; -fx-background-radius:8 8 0 0;"
                + "-fx-border-color:" + C_BORDER
                + "; -fx-border-radius:8 8 0 0; -fx-border-width:1 1 0 1;");
        tableHeader.setPadding(new Insets(10, 16, 10, 16));
        tableHeader.getChildren().addAll(
                tableHeaderCell("Room #",   70),
                tableHeaderCell("Type",     120),
                tableHeaderCell("Status",   120),
                tableHeaderCell("Guest",    Double.MAX_VALUE)
        );

        VBox tableBody = new VBox(0);
        var allRooms = manager.getAllRooms();

        if (allRooms.isEmpty()) {
            HBox emptyRow = new HBox();
            emptyRow.setPadding(new Insets(20, 16, 20, 16));
            emptyRow.setStyle("-fx-background-color:" + C_CARD
                    + "; -fx-border-color:" + C_BORDER + "; -fx-border-width:0 1 1 1;");
            Label none = new Label("No rooms registered yet. Add rooms to get started.");
            none.setFont(Font.font("Courier New", 12));
            none.setTextFill(Color.web(C_MUTED));
            emptyRow.getChildren().add(none);
            tableBody.getChildren().add(emptyRow);
        } else {
            for (int i = 0; i < allRooms.size(); i++) {
                Room r = allRooms.get(i);
                boolean isLast = (i == allRooms.size() - 1);
                tableBody.getChildren().add(tableDataRow(r, i % 2 == 0, isLast));
            }
        }

        VBox table = new VBox(0);
        table.getChildren().addAll(tableHeader, tableBody);

        Button refreshBtn = secondaryBtn("↺  Refresh");
        refreshBtn.setOnAction(e -> navigateTo("allRooms"));

        page.getChildren().addAll(header, refreshBtn, table);
        scroll.setContent(page);
        return scroll;
    }

    private Label tableHeaderCell(String text, double width) {
        Label l = new Label(text.toUpperCase());
        l.setFont(Font.font("Courier New", FontWeight.BOLD, 10));
        l.setTextFill(Color.web(C_MUTED));
        if (width == Double.MAX_VALUE) HBox.setHgrow(l, Priority.ALWAYS);
        else l.setMinWidth(width);
        return l;
    }

    private HBox tableDataRow(Room r, boolean even, boolean isLast) {
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));

        boolean avail = r.isAvailable();
        String rowBg = even ? C_CARD : C_BG;
        String radius = isLast ? "0 0 8 8" : "0";
        String bw = isLast ? "0 1 1 1" : "0 1 0 1";

        row.setStyle("-fx-background-color:" + rowBg
                + "; -fx-background-radius:" + radius
                + "; -fx-border-color:" + C_BORDER
                + "; -fx-border-radius:" + radius
                + "; -fx-border-width:" + bw + ";");

        Label num = new Label("#" + r.getRoomNumber());
        num.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        num.setTextFill(Color.web(C_ACCENT));
        num.setMinWidth(70);

        Label type = new Label(r.getRoomType().toString());
        type.setFont(Font.font("Courier New", 12));
        type.setTextFill(Color.web(C_TEXT));
        type.setMinWidth(120);

        HBox statusBadge = new HBox(4);
        statusBadge.setAlignment(Pos.CENTER_LEFT);
        statusBadge.setMinWidth(120);
        Label dot = new Label("●");
        dot.setFont(Font.font("System", 10));
        dot.setTextFill(Color.web(avail ? C_GREEN : C_RED));
        Label statusText = new Label(avail ? "Available" : "Occupied");
        statusText.setFont(Font.font("Courier New", 12));
        statusText.setTextFill(Color.web(avail ? C_GREEN : C_RED));
        statusBadge.getChildren().addAll(dot, statusText);

        Label guest = new Label(avail ? "—"
                : (r.getGuestName() == null || r.getGuestName().isEmpty() ? "Guest" : r.getGuestName()));
        guest.setFont(Font.font("Courier New", 12));
        guest.setTextFill(Color.web(C_MUTED));
        HBox.setHgrow(guest, Priority.ALWAYS);

        row.getChildren().addAll(num, type, statusBadge, guest);
        return row;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  REUSABLE FORM HELPERS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Returns a ScrollPane whose content is a VBox pre-loaded with a page heading.
     * Callers do:  ((VBox) scroll.getContent()).getChildren().add(formCard);
     */
    private ScrollPane centeredFormPage(String pageTitle, String pageSubtitle) {
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:" + C_BG + "; -fx-background:" + C_BG + ";");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox outer = new VBox(28);
        outer.setPadding(new Insets(36));
        outer.setStyle("-fx-background-color:" + C_BG + ";");

        VBox header = new VBox(4);
        Label t = new Label(pageTitle);
        t.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        t.setTextFill(Color.web(C_TEXT));
        Label s = new Label(pageSubtitle);
        s.setFont(Font.font("Courier New", 12));
        s.setTextFill(Color.web(C_MUTED));
        header.getChildren().addAll(t, s);
        outer.getChildren().add(header);

        scroll.setContent(outer);
        return scroll;
    }

    private VBox formCard() {
        VBox card = new VBox(18);
        card.setPadding(new Insets(28, 32, 28, 32));
        card.setMaxWidth(560);
        card.setStyle("-fx-background-color:" + C_CARD
                + "; -fx-background-radius:12;"
                + "-fx-border-color:" + C_BORDER
                + "; -fx-border-radius:12; -fx-border-width:1;");
        return card;
    }

    private VBox formRow(String labelText, javafx.scene.Node field) {
        VBox row = new VBox(6);
        Label lbl = new Label(labelText);
        lbl.setFont(Font.font("Courier New", FontWeight.BOLD, 11));
        lbl.setTextFill(Color.web(C_MUTED));
        row.getChildren().addAll(lbl, field);
        return row;
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setFont(Font.font("Courier New", 13));
        tf.setStyle("-fx-background-color:" + C_BG
                + "; -fx-text-fill:" + C_TEXT
                + "; -fx-prompt-text-fill:" + C_MUTED
                + "; -fx-border-color:" + C_BORDER
                + "; -fx-border-radius:6; -fx-background-radius:6;"
                + "-fx-padding: 9 12;");
        tf.setOnMouseEntered(e -> tf.setStyle("-fx-background-color:" + C_BG
                + "; -fx-text-fill:" + C_TEXT
                + "; -fx-prompt-text-fill:" + C_MUTED
                + "; -fx-border-color:" + C_ACCENT
                + "; -fx-border-radius:6; -fx-background-radius:6;"
                + "-fx-padding: 9 12;"));
        tf.setOnMouseExited(e -> tf.setStyle("-fx-background-color:" + C_BG
                + "; -fx-text-fill:" + C_TEXT
                + "; -fx-prompt-text-fill:" + C_MUTED
                + "; -fx-border-color:" + C_BORDER
                + "; -fx-border-radius:6; -fx-background-radius:6;"
                + "-fx-padding: 9 12;"));
        return tf;
    }

    private <T> void styleCombo(ComboBox<T> cb) {
        cb.setMaxWidth(Double.MAX_VALUE);
        cb.setStyle("-fx-background-color:" + C_BG
                + "; -fx-text-fill:" + C_TEXT
                + "; -fx-border-color:" + C_BORDER
                + "; -fx-border-radius:6; -fx-background-radius:6;");
    }

    private Label feedbackLabel() {
        Label l = new Label();
        l.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
        l.setWrapText(true);
        l.setVisible(false);
        l.setManaged(false);
        return l;
    }

    private void feedbackOk(Label l, String msg) {
        l.setText(msg);
        l.setTextFill(Color.web(C_GREEN));
        l.setVisible(true); l.setManaged(true);
        FadeTransition ft = new FadeTransition(Duration.millis(300), l);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }

    private void feedbackError(Label l, String msg) {
        l.setText(msg);
        l.setTextFill(Color.web(C_RED));
        l.setVisible(true); l.setManaged(true);
        // shake
        TranslateTransition shake = new TranslateTransition(Duration.millis(60), l);
        shake.setFromX(0); shake.setByX(8); shake.setCycleCount(4);
        shake.setAutoReverse(true); shake.play();
    }

    private Button primaryBtn(String text) {
        Button b = new Button(text);
        b.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-background-color:" + C_ACCENT
                + "; -fx-text-fill:#000000;"
                + "-fx-background-radius:8; -fx-padding: 11 0; -fx-cursor:hand;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color:" + C_ACCENT2
                + "; -fx-text-fill:#000000;"
                + "-fx-background-radius:8; -fx-padding: 11 0; -fx-cursor:hand;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color:" + C_ACCENT
                + "; -fx-text-fill:#000000;"
                + "-fx-background-radius:8; -fx-padding: 11 0; -fx-cursor:hand;"));
        return b;
    }

    private Button dangerBtn(String text) {
        Button b = new Button(text);
        b.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-background-color:" + C_RED
                + "; -fx-text-fill:#ffffff;"
                + "-fx-background-radius:8; -fx-padding: 11 0; -fx-cursor:hand;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color:#c0392b"
                + "; -fx-text-fill:#ffffff;"
                + "-fx-background-radius:8; -fx-padding: 11 0; -fx-cursor:hand;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color:" + C_RED
                + "; -fx-text-fill:#ffffff;"
                + "-fx-background-radius:8; -fx-padding: 11 0; -fx-cursor:hand;"));
        return b;
    }

    private Button secondaryBtn(String text) {
        Button b = new Button(text);
        b.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
        b.setStyle("-fx-background-color:transparent"
                + "; -fx-text-fill:" + C_TEXT
                + "; -fx-border-color:" + C_BORDER
                + "; -fx-border-radius:8; -fx-background-radius:8;"
                + "-fx-padding: 8 18; -fx-cursor:hand;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color:" + C_CARD
                + "; -fx-text-fill:" + C_TEXT
                + "; -fx-border-color:" + C_ACCENT
                + "; -fx-border-radius:8; -fx-background-radius:8;"
                + "-fx-padding: 8 18; -fx-cursor:hand;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color:transparent"
                + "; -fx-text-fill:" + C_TEXT
                + "; -fx-border-color:" + C_BORDER
                + "; -fx-border-radius:8; -fx-background-radius:8;"
                + "-fx-padding: 8 18; -fx-cursor:hand;"));
        return b;
    }

    private Label pageSection(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Courier New", FontWeight.BOLD, 11));
        l.setTextFill(Color.web(C_MUTED));
        l.setPadding(new Insets(4, 0, 0, 0));
        return l;
    }

    private Separator styledSeparator() {
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:" + C_BORDER + ";");
        return sep;
    }

    private void addHoverLift(Region node) {
        node.setOnMouseEntered(e -> {
            node.setStyle(node.getStyle().replace(C_CARD, C_CARD_HOVER));
            ScaleTransition st = new ScaleTransition(Duration.millis(120), node);
            st.setToX(1.015); st.setToY(1.015); st.play();
        });
        node.setOnMouseExited(e -> {
            node.setStyle(node.getStyle().replace(C_CARD_HOVER, C_CARD));
            ScaleTransition st = new ScaleTransition(Duration.millis(120), node);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });
    }
}