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
    private static final String D_BG         = "#0d1117";
    private static final String D_SIDEBAR    = "#161b22";
    private static final String D_CARD       = "#1c2333";
    private static final String D_CARD_HOVER = "#21293a";
    private static final String D_BORDER     = "#30363d";
    private static final String D_ACCENT     = "#f0a500";
    private static final String D_ACCENT2    = "#e06c00";
    private static final String D_TEXT       = "#e6edf3";
    private static final String D_MUTED      = "#8b949e";
    private static final String D_GREEN      = "#3fb950";
    private static final String D_RED        = "#f85149";
    private static final String D_BLUE       = "#58a6ff";

    private static final String L_BG         = "#f5f7fb";
    private static final String L_SIDEBAR    = "#e8edf5";
    private static final String L_CARD       = "#ffffff";
    private static final String L_CARD_HOVER = "#f1f5fb";
    private static final String L_BORDER     = "#cfd8e3";
    private static final String L_ACCENT     = "#b77a00";
    private static final String L_ACCENT2    = "#8c5f00";
    private static final String L_TEXT       = "#1f2937";
    private static final String L_MUTED      = "#64748b";
    private static final String L_GREEN      = "#1d9b5f";
    private static final String L_RED        = "#d14444";
    private static final String L_BLUE       = "#2563eb";

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    private String C_BG = D_BG;
    private String C_SIDEBAR = D_SIDEBAR;
    private String C_CARD = D_CARD;
    private String C_CARD_HOVER = D_CARD_HOVER;
    private String C_BORDER = D_BORDER;
    private String C_ACCENT = D_ACCENT;
    private String C_ACCENT2 = D_ACCENT2;
    private String C_TEXT = D_TEXT;
    private String C_MUTED = D_MUTED;
    private String C_GREEN = D_GREEN;
    private String C_RED = D_RED;
    private String C_BLUE = D_BLUE;

    // ── state ──────────────────────────────────────────────────────────────────
    private HotelManager manager;
    private BorderPane rootView;
    private StackPane contentArea;
    private String activePage = "home";
    private String hotelName = "Lorem Ipsum";
    private boolean darkMode = true;
    private boolean adminAuthenticated = false;
    private String pendingProtectedPage = null;

    // sidebar nav buttons kept for highlight toggling
    private Button navHome, navAddRoom, navBookRoom, navCheckout, navAllRooms, navSettings;

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
        sidebar.setPrefWidth(250);
        sidebar.setMinWidth(250);
        sidebar.setStyle("-fx-background-color:" + C_SIDEBAR
                + "; -fx-border-color:" + C_BORDER
                + "; -fx-border-width: 0 1 0 0;");

        // ── branding ──────────────────────────────────────────────────────────
        VBox brand = new VBox(6);
        brand.setPadding(new Insets(24, 20, 20, 20));
        brand.setStyle("-fx-background-color:" + C_CARD + ";"
            + "-fx-background-radius:12;"
            + "-fx-border-color:" + C_BORDER + ";"
            + "-fx-border-radius:12;"
            + "-fx-border-width:1;");
        brand.setEffect(new DropShadow(12, Color.web("#00000066")));

        Label hotelIcon = new Label("⌂");
        hotelIcon.setFont(Font.font("Georgia", FontWeight.BOLD, 36));
        hotelIcon.setTextFill(Color.web(C_ACCENT));

        Label brandName = new Label(hotelName);
        brandName.setFont(Font.font("Georgia", FontWeight.EXTRA_BOLD, 28));
        brandName.setTextFill(Color.web(C_TEXT));
        brandName.setStyle("-fx-letter-spacing: 0.8px;");

        Label brandSub = new Label("Hotel Management");
        brandSub.setFont(Font.font("Avenir Next", FontWeight.MEDIUM, 12));
        brandSub.setTextFill(Color.web(C_MUTED));

        brand.getChildren().addAll(hotelIcon, brandName, brandSub);

        // ── divider ───────────────────────────────────────────────────────────
        Separator sep1 = styledSeparator();

        // ── section label ─────────────────────────────────────────────────────
        Label navLabel = new Label("NAVIGATION");
        navLabel.setFont(Font.font("Avenir Next", FontWeight.BOLD, 10));
        navLabel.setTextFill(Color.web(C_MUTED));
        navLabel.setPadding(new Insets(12, 20, 6, 20));

        // ── nav buttons ───────────────────────────────────────────────────────
        navHome     = navBtn("⌂", "Dashboard",   "home");
        navAddRoom  = navBtn("+", "Add Room",     "addRoom");
        navBookRoom = navBtn("⊞", "Book Room",    "bookRoom");
        navCheckout = navBtn("⊟", "Checkout",     "checkout");
        navAllRooms = navBtn("≡", "All Rooms",    "allRooms");
        navSettings = navBtn("⚙", "Settings",     "settings");

        // ── spacer + footer ───────────────────────────────────────────────────
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Separator sep2 = styledSeparator();

        Label footer = new Label("OOSD Week 10 Project");
        footer.setFont(Font.font("Avenir Next", 10));
        footer.setTextFill(Color.web(C_MUTED));
        footer.setPadding(new Insets(12, 20, 20, 20));

        sidebar.getChildren().addAll(brand, sep1, navLabel,
            navHome, navAddRoom, navBookRoom, navCheckout, navAllRooms, navSettings,
                spacer, sep2, footer);
        return sidebar;
    }

    private Button navBtn(String icon, String label, String pageId) {
        HBox inner = new HBox(12);
        inner.setAlignment(Pos.CENTER_LEFT);

        Label ic = new Label(icon);
        ic.setFont(Font.font("Georgia", FontWeight.BOLD, 15));
        ic.setMinWidth(20);
        ic.setTextFill(Color.web(navIconColor(pageId)));

        Label lbl = new Label(label);
        lbl.setFont(Font.font("Avenir Next", FontWeight.NORMAL, 14));
        lbl.setTextFill(Color.web(C_TEXT));

        inner.getChildren().addAll(ic, lbl);

        Button btn = new Button();
        btn.setGraphic(inner);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(11, 20, 11, 20));
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setStyle(navBtnStyle(false));

        btn.setOnMouseEntered(e -> {
            if (!activePage.equals(pageId)) {
                btn.setStyle(navBtnHoverStyle());
                setNavColors(btn, pageId, false, true);
            }
        });
        btn.setOnMouseExited(e -> {
            if (!activePage.equals(pageId)) {
                btn.setStyle(navBtnStyle(false));
                setNavColors(btn, pageId, false, false);
            } else {
                btn.setStyle(navBtnStyle(true));
                setNavColors(btn, pageId, true, false);
            }
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
        if (("addRoom".equals(pageId) || "settings".equals(pageId)) && !adminAuthenticated) {
            pendingProtectedPage = pageId;
            pageId = "adminLogin";
        }

        activePage = pageId;
        highlightNav(pageId);

        javafx.scene.Node page = switch (pageId) {
            case "addRoom"  -> buildAddRoomPage();
            case "bookRoom" -> buildBookRoomPage();
            case "checkout" -> buildCheckoutPage();
            case "allRooms" -> buildAllRoomsPage();
            case "settings" -> buildSettingsPage();
            case "adminLogin" -> buildAdminLoginPage();
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
        Button[] btns = {navHome, navAddRoom, navBookRoom, navCheckout, navAllRooms, navSettings};
        String[] ids  = {"home", "addRoom", "bookRoom", "checkout", "allRooms", "settings"};
        for (int i = 0; i < btns.length; i++) {
            btns[i].setStyle(navBtnStyle(ids[i].equals(pageId)));
            setNavColors(btns[i], ids[i], ids[i].equals(pageId), false);
        }
    }

    private void setNavColors(Button btn, String pageId, boolean active, boolean hovered) {
        HBox hb = (HBox) btn.getGraphic();
        Label ic = (Label) hb.getChildren().get(0);
        Label lbl = (Label) hb.getChildren().get(1);
        ic.setTextFill(Color.web(navIconColor(pageId)));
        lbl.setTextFill(Color.web(active || hovered ? C_ACCENT : C_TEXT));
    }

    private String navIconColor(String pageId) {
        return switch (pageId) {
            case "addRoom" -> C_GREEN;
            case "checkout" -> C_RED;
            case "bookRoom" -> C_BLUE;
            case "allRooms" -> C_ACCENT;
            case "settings" -> C_MUTED;
            default -> C_ACCENT;
        };
    }

    private javafx.scene.Node buildAdminLoginPage() {
        ScrollPane page = centeredFormPage("Admin Login", "Sign in to access Add Room and Settings");
        VBox outer = (VBox) page.getContent();

        VBox card = formCard();

        Label info = new Label("Admin access is required for these sections.");
        info.setFont(Font.font("Avenir Next", FontWeight.BOLD, 12));
        info.setStyle("-fx-text-fill: " + C_RED + ";");

        TextField txtUsername = styledField("Username");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Password");
        txtPassword.setFont(Font.font("Avenir Next", 14));
        txtPassword.setStyle("-fx-background-color:" + C_BG
                + "; -fx-text-fill:" + C_TEXT
                + "; -fx-prompt-text-fill:" + C_MUTED
                + "; -fx-border-color:" + C_BORDER
                + "; -fx-border-radius:6; -fx-background-radius:6;"
                + "-fx-padding: 9 12;");

        Label feedback = feedbackLabel();
        Button btnLogin = primaryBtn("Log In");
        Button btnBack = secondaryBtn("Back to Dashboard");

        btnLogin.setOnAction(e -> {
            String user = txtUsername.getText().trim();
            String pass = txtPassword.getText();
            if (ADMIN_USERNAME.equals(user) && ADMIN_PASSWORD.equals(pass)) {
                adminAuthenticated = true;
                feedbackOk(feedback, "✔  Login successful.");
                String destination = pendingProtectedPage == null ? "home" : pendingProtectedPage;
                pendingProtectedPage = null;
                navigateTo(destination);
            } else {
                feedbackError(feedback, "⚠  Invalid credentials. Use admin / admin123.");
            }
        });

        btnBack.setOnAction(e -> {
            pendingProtectedPage = null;
            navigateTo("home");
        });

        card.getChildren().addAll(
                info,
                formRow("Admin Username", txtUsername),
                formRow("Admin Password", txtPassword),
                feedback,
                btnLogin,
                btnBack
        );

        outer.getChildren().add(card);
        return page;
    }

    private javafx.scene.Node buildSettingsPage() {
        ScrollPane page = centeredFormPage("Settings", "Update hotel branding and interface theme");
        VBox outer = (VBox) page.getContent();

        VBox card = formCard();

        TextField txtHotelName = styledField("Hotel Name");
        txtHotelName.setText(hotelName);

        TextField txtStandardTariff = styledField("Standard base tariff");
        txtStandardTariff.setText(String.format("%.2f", manager.getStandardBaseTariff()));

        TextField txtDeluxeTariff = styledField("Deluxe base tariff");
        txtDeluxeTariff.setText(String.format("%.2f", manager.getDeluxeBaseTariff()));

        ComboBox<String> cbTheme = new ComboBox<>();
        cbTheme.getItems().addAll("Dark Mode", "Light Mode");
        cbTheme.setValue(darkMode ? "Dark Mode" : "Light Mode");
        styleCombo(cbTheme);

        Label settingsFeedback = feedbackLabel();
        Button btnSave = primaryBtn("Save Settings");
        Button btnLogout = secondaryBtn("Logout");

        btnSave.setOnAction(e -> {
            String newHotelName = txtHotelName.getText().trim();
            if (newHotelName.isEmpty()) {
                feedbackError(settingsFeedback, "⚠  Hotel name cannot be empty.");
                return;
            }

            double standardTariff;
            double deluxeTariff;
            try {
                standardTariff = Double.parseDouble(txtStandardTariff.getText().trim());
                deluxeTariff = Double.parseDouble(txtDeluxeTariff.getText().trim());
            } catch (NumberFormatException ex) {
                feedbackError(settingsFeedback, "⚠  Tariffs must be valid numbers.");
                return;
            }

            if (!manager.updateBaseTariffs(standardTariff, deluxeTariff)) {
                feedbackError(settingsFeedback, "⚠  Tariffs must be greater than zero.");
                return;
            }

            hotelName = newHotelName;
            boolean selectedDark = "Dark Mode".equals(cbTheme.getValue());
            applyTheme(selectedDark);
            feedbackOk(settingsFeedback, "✔  Settings and tariffs updated.");
        });

        btnLogout.setOnAction(e -> {
            adminAuthenticated = false;
            pendingProtectedPage = null;
            navigateTo("settings");
        });

        card.getChildren().addAll(
                styledSeparator(),
                formRow("Hotel Name", txtHotelName),
                formRow("Standard Base Tariff", txtStandardTariff),
                formRow("Deluxe Base Tariff", txtDeluxeTariff),
                formRow("Theme", cbTheme),
                settingsFeedback,
                btnSave,
                btnLogout
        );

        outer.getChildren().add(card);
        return page;
    }

    private void applyTheme(boolean useDarkMode) {
        darkMode = useDarkMode;
        if (darkMode) {
            C_BG = D_BG;
            C_SIDEBAR = D_SIDEBAR;
            C_CARD = D_CARD;
            C_CARD_HOVER = D_CARD_HOVER;
            C_BORDER = D_BORDER;
            C_ACCENT = D_ACCENT;
            C_ACCENT2 = D_ACCENT2;
            C_TEXT = D_TEXT;
            C_MUTED = D_MUTED;
            C_GREEN = D_GREEN;
            C_RED = D_RED;
            C_BLUE = D_BLUE;
        } else {
            C_BG = L_BG;
            C_SIDEBAR = L_SIDEBAR;
            C_CARD = L_CARD;
            C_CARD_HOVER = L_CARD_HOVER;
            C_BORDER = L_BORDER;
            C_ACCENT = L_ACCENT;
            C_ACCENT2 = L_ACCENT2;
            C_TEXT = L_TEXT;
            C_MUTED = L_MUTED;
            C_GREEN = L_GREEN;
            C_RED = L_RED;
            C_BLUE = L_BLUE;
        }

        if (rootView != null) {
            rootView.setStyle("-fx-background-color:" + C_BG + ";");
            rootView.setLeft(buildSidebar());
            contentArea.setStyle("-fx-background-color:" + C_BG + ";");
            navigateTo(activePage);
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
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 30));
        title.setTextFill(Color.web(C_TEXT));

        Label subtitle = new Label("Live overview of hotel operations");
        subtitle.setFont(Font.font("Avenir Next", 13));
        subtitle.setTextFill(Color.web(C_MUTED));
        titleBox.getChildren().addAll(title, subtitle);

        Region hSpacer = new Region();
        HBox.setHgrow(hSpacer, Priority.ALWAYS);

        // live clock
        Label clock = new Label();
        clock.setFont(Font.font("Avenir Next", FontWeight.BOLD, 13));
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
            statCard("Total Rooms",   String.valueOf(total)),
            statCard("Available",     String.valueOf(available)),
            statCard("Occupied",      String.valueOf(occupied)),
            statCard("Guests Today",  String.valueOf(occupied))
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
            none.setFont(Font.font("Avenir Next", 13));
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

    private VBox statCard(String label, String value) {
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
    StackPane iconChip = iconChip(statIcon(label), statAccent(label), 20, 11);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Avenir Next", 12));
        lbl.setTextFill(Color.web(C_MUTED));
    top.getChildren().addAll(iconChip, lbl);

        Label val = new Label(value);
        val.setFont(Font.font("Georgia", FontWeight.BOLD, 38));
        val.setTextFill(Color.web(statAccent(label)));

        card.getChildren().addAll(top, val);
        addHoverLift(card);
        return card;
    }

    private StackPane iconChip(String icon, String accentColor, double size, double fontSize) {
        StackPane chip = new StackPane();
        chip.setMinSize(size, size);
        chip.setPrefSize(size, size);
        chip.setMaxSize(size, size);
        chip.setStyle("-fx-background-color:" + accentColor + ";"
                + "-fx-background-radius:999;");

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Georgia", FontWeight.BOLD, fontSize));
        iconLabel.setTextFill(Color.web("#ffffff"));
        chip.getChildren().add(iconLabel);
        return chip;
    }

    private String statAccent(String label) {
        return switch (label) {
            case "Available" -> C_GREEN;
            case "Occupied" -> C_RED;
            case "Total Rooms" -> C_BLUE;
            case "Guests Today" -> C_ACCENT;
            default -> C_TEXT;
        };
    }

    private String statIcon(String label) {
        return switch (label) {
            case "Available" -> "◉";
            case "Occupied" -> "◎";
            case "Total Rooms" -> "⬡";
            case "Guests Today" -> "★";
            default -> "•";
        };
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

        String accent = actionIconColor(targetPage);
        StackPane iconChip = iconChip(icon, accent, 30, 16);

        Label t = new Label(title);
        t.setFont(Font.font("Avenir Next", FontWeight.BOLD, 14));
        t.setTextFill(Color.web(C_TEXT));

        Label d = new Label(desc);
        d.setFont(Font.font("Avenir Next", 12));
        d.setTextFill(Color.web(C_MUTED));
        d.setWrapText(true);

        card.getChildren().addAll(iconChip, t, d);
        card.setOnMouseClicked(e -> navigateTo(targetPage));
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color:" + C_CARD_HOVER
                        + "; -fx-background-radius:10;"
                + "-fx-border-color:" + accent
                        + "; -fx-border-radius:10; -fx-border-width:1; -fx-cursor:hand;"));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color:" + C_CARD
                        + "; -fx-background-radius:10;"
                        + "-fx-border-color:" + C_BORDER
                        + "; -fx-border-radius:10; -fx-border-width:1; -fx-cursor:hand;"));
        return card;
    }

    private String actionIconColor(String targetPage) {
        return switch (targetPage) {
            case "addRoom" -> C_GREEN;
            case "checkout" -> C_RED;
            case "bookRoom" -> C_BLUE;
            case "allRooms" -> C_ACCENT;
            default -> C_ACCENT;
        };
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
        roomNum.setFont(Font.font("Avenir Next", FontWeight.BOLD, 15));
        roomNum.setTextFill(Color.web(C_ACCENT));
        roomNum.setMinWidth(50);

        Label type = new Label(r.getRoomType().toString());
        type.setFont(Font.font("Avenir Next", 13));
        type.setTextFill(Color.web(C_TEXT));

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        HBox badge = new HBox(6);
        badge.setAlignment(Pos.CENTER_LEFT);
        badge.setPadding(new Insets(3, 10, 3, 10));
        badge.setStyle("-fx-background-color:" + C_GREEN + "33;"
            + "-fx-background-radius:999;"
            + "-fx-border-color:" + C_GREEN + "66;"
            + "-fx-border-radius:999;"
            + "-fx-border-width:1;");

        Label dot = new Label("●");
        dot.setFont(Font.font("System", 11));
        dot.setTextFill(Color.web(C_GREEN));

        Label badgeText = new Label("Available");
        badgeText.setFont(Font.font("Avenir Next", FontWeight.BOLD, 12));
        badgeText.setTextFill(Color.web(C_GREEN));

        badge.getChildren().addAll(dot, badgeText);

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
                boolean added = manager.addRoom(newRoom);
                if (added) {
                    feedbackOk(feedback, "✔  Room " + roomNo + " added successfully.");
                    txtRoomNumber.clear();
                    cbType.setValue(null);
                } else {
                    feedbackError(feedback, "⚠  Room number " + roomNo + " already exists.");
                }
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
        TextField txtDaysStayed = styledField("Days Stayed");
        Label feedback = feedbackLabel();
        Label billSummary = feedbackLabel();
        Button btnCheckout = dangerBtn("Confirm Checkout");

        btnCheckout.setOnAction(e -> {
            String s = txtRoomNo.getText().trim();
            String daysText = txtDaysStayed.getText().trim();
            if (s.isEmpty()) {
                feedbackError(feedback, "⚠  Please enter a room number.");
                return;
            }
            if (daysText.isEmpty()) {
                feedbackError(feedback, "⚠  Please enter the number of days stayed.");
                return;
            }
            try {
                int roomNo = Integer.parseInt(s);
                int daysStayed = Integer.parseInt(daysText);
                if (daysStayed <= 0) {
                    feedbackError(feedback, "⚠  Days stayed must be at least 1.");
                    return;
                }

                Room room = manager.getRoomByNumber(roomNo);
                Customer customer = manager.getBookedCustomer(roomNo);

                if (room == null || room.isAvailable()) {
                    feedbackError(feedback, "⚠  Room " + roomNo + " is not currently booked.");
                    billSummary.setVisible(false);
                    billSummary.setManaged(false);
                    return;
                }

                double billAmount = manager.calculateBillAmount(room, daysStayed);

                if (manager.checkout(roomNo)) {
                    String guestName = (customer == null || customer.getName() == null || customer.getName().isEmpty())
                            ? "Guest"
                            : customer.getName();
                    feedbackOk(feedback, "✔  Room " + roomNo + " checked out successfully for " + guestName + ".");
                    billSummary.setText(String.format("Bill Amount: Rs. %.2f for %d day(s)", billAmount, daysStayed));
                    billSummary.setTextFill(Color.web(C_BLUE));
                    billSummary.setVisible(true);
                    billSummary.setManaged(true);
                    txtRoomNo.clear();
                    txtDaysStayed.clear();
                } else {
                    feedbackError(feedback, "⚠  Room " + roomNo + " is not currently booked.");
                    billSummary.setVisible(false);
                    billSummary.setManaged(false);
                }
            } catch (NumberFormatException ex) {
                feedbackError(feedback, "⚠  Room number and days stayed must be valid integers.");
                billSummary.setVisible(false);
                billSummary.setManaged(false);
            }
        });

        form.getChildren().addAll(
                formRow("Room Number", txtRoomNo),
                formRow("Days Stayed", txtDaysStayed),
                feedback,
                billSummary,
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
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 30));
        title.setTextFill(Color.web(C_TEXT));
        Label sub = new Label("Full inventory of hotel rooms");
        sub.setFont(Font.font("Avenir Next", 13));
        sub.setTextFill(Color.web(C_MUTED));
        header.getChildren().addAll(title, sub);

        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = styledField("Search room #, type, or guest");
        searchField.setPrefWidth(260);

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Status", "Available", "Occupied");
        statusFilter.setValue("All Status");
        styleCombo(statusFilter);
        statusFilter.setPrefWidth(150);

        ComboBox<String> typeFilter = new ComboBox<>();
        typeFilter.getItems().add("All Types");
        for (RoomType roomType : RoomType.values()) {
            typeFilter.getItems().add(roomType.toString());
        }
        typeFilter.setValue("All Types");
        styleCombo(typeFilter);
        typeFilter.setPrefWidth(150);

        Button clearFilters = secondaryBtn("Clear Filters");

        filterBar.getChildren().addAll(searchField, statusFilter, typeFilter, clearFilters);

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
        Runnable refreshTable = () -> {
            tableBody.getChildren().clear();

            String query = searchField.getText() == null ? "" : searchField.getText().trim();
            String selectedStatus = statusFilter.getValue();
            String selectedType = typeFilter.getValue();

            var filteredRooms = filterRooms(manager.getAllRooms(), query, selectedStatus, selectedType);

            if (filteredRooms.isEmpty()) {
                HBox emptyRow = new HBox();
                emptyRow.setPadding(new Insets(20, 16, 20, 16));
                emptyRow.setStyle("-fx-background-color:" + C_CARD
                        + "; -fx-border-color:" + C_BORDER + "; -fx-border-width:0 1 1 1;");
                Label none = new Label(query.isEmpty() && "All Status".equals(selectedStatus) && "All Types".equals(selectedType)
                        ? "No rooms registered yet. Add rooms to get started."
                        : "No rooms match the selected filters.");
                none.setFont(Font.font("Avenir Next", 13));
                none.setTextFill(Color.web(C_MUTED));
                emptyRow.getChildren().add(none);
                tableBody.getChildren().add(emptyRow);
            } else {
                for (int i = 0; i < filteredRooms.size(); i++) {
                    Room r = filteredRooms.get(i);
                    boolean isLast = (i == filteredRooms.size() - 1);
                    tableBody.getChildren().add(tableDataRow(r, i % 2 == 0, isLast));
                }
            }
        };

        searchField.textProperty().addListener((obs, oldValue, newValue) -> refreshTable.run());
        statusFilter.valueProperty().addListener((obs, oldValue, newValue) -> refreshTable.run());
        typeFilter.valueProperty().addListener((obs, oldValue, newValue) -> refreshTable.run());
        clearFilters.setOnAction(e -> {
            searchField.clear();
            statusFilter.setValue("All Status");
            typeFilter.setValue("All Types");
            refreshTable.run();
        });
        refreshTable.run();

        VBox table = new VBox(0);
        table.getChildren().addAll(tableHeader, tableBody);

        Button refreshBtn = secondaryBtn("Refresh");
        Label refreshIcon = new Label("↺");
        refreshIcon.setTextFill(Color.web(C_BLUE));
        refreshIcon.setFont(Font.font("Georgia", FontWeight.BOLD, 15));
        refreshBtn.setGraphic(refreshIcon);
        refreshBtn.setContentDisplay(ContentDisplay.LEFT);
        refreshBtn.setGraphicTextGap(6);
        refreshBtn.setOnAction(e -> navigateTo("allRooms"));

        page.getChildren().addAll(header, filterBar, refreshBtn, table);
        scroll.setContent(page);
        return scroll;
    }

    private java.util.List<Room> filterRooms(java.util.List<Room> rooms, String query, String statusFilter, String typeFilter) {
        java.util.List<Room> filtered = new java.util.ArrayList<>();
        String normalizedQuery = query.toLowerCase();

        for (Room room : rooms) {
            Customer bookedCustomer = manager.getBookedCustomer(room.getRoomNumber());
            String guestName = bookedCustomer == null ? "" : bookedCustomer.getName();
            boolean matchesQuery = normalizedQuery.isEmpty()
                    || String.valueOf(room.getRoomNumber()).contains(normalizedQuery)
                    || room.getRoomType().toString().toLowerCase().contains(normalizedQuery)
                    || (!guestName.isEmpty() && guestName.toLowerCase().contains(normalizedQuery));

            boolean matchesStatus = "All Status".equals(statusFilter)
                    || ("Available".equals(statusFilter) && room.isAvailable())
                    || ("Occupied".equals(statusFilter) && !room.isAvailable());

            boolean matchesType = "All Types".equals(typeFilter)
                    || room.getRoomType().toString().equalsIgnoreCase(typeFilter);

            if (matchesQuery && matchesStatus && matchesType) {
                filtered.add(room);
            }
        }

        return filtered;
    }

    private Label tableHeaderCell(String text, double width) {
        Label l = new Label(text.toUpperCase());
        l.setFont(Font.font("Avenir Next", FontWeight.BOLD, 11));
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
        num.setFont(Font.font("Avenir Next", FontWeight.BOLD, 14));
        num.setTextFill(Color.web(C_ACCENT));
        num.setMinWidth(70);

        Label type = new Label(r.getRoomType().toString());
        type.setFont(Font.font("Avenir Next", 13));
        type.setTextFill(Color.web(C_TEXT));
        type.setMinWidth(120);

        HBox statusBadge = new HBox(4);
        statusBadge.setAlignment(Pos.CENTER_LEFT);
        statusBadge.setMinWidth(120);
        Label dot = new Label("●");
        dot.setFont(Font.font("System", 11));
        dot.setTextFill(Color.web(avail ? C_GREEN : C_RED));
        Label statusText = new Label(avail ? "Available" : "Occupied");
        statusText.setFont(Font.font("Avenir Next", 13));
        statusText.setTextFill(Color.web(avail ? C_GREEN : C_RED));
        statusBadge.getChildren().addAll(dot, statusText);

        Customer bookedCustomer = manager.getBookedCustomer(r.getRoomNumber());
        String guestName = (bookedCustomer == null || bookedCustomer.getName() == null || bookedCustomer.getName().isEmpty())
            ? "Guest"
            : bookedCustomer.getName();
        Label guest = new Label(avail ? "—" : guestName);
        guest.setFont(Font.font("Avenir Next", 13));
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
        t.setFont(Font.font("Georgia", FontWeight.BOLD, 30));
        t.setTextFill(Color.web(C_TEXT));
        Label s = new Label(pageSubtitle);
        s.setFont(Font.font("Avenir Next", 13));
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
        lbl.setFont(Font.font("Avenir Next", FontWeight.BOLD, 12));
        lbl.setTextFill(Color.web(C_MUTED));
        row.getChildren().addAll(lbl, field);
        return row;
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setFont(Font.font("Avenir Next", 14));
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
        l.setFont(Font.font("Avenir Next", FontWeight.BOLD, 13));
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
        b.setFont(Font.font("Avenir Next", FontWeight.BOLD, 14));
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
        b.setFont(Font.font("Avenir Next", FontWeight.BOLD, 14));
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
        b.setFont(Font.font("Avenir Next", FontWeight.BOLD, 13));
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
        l.setFont(Font.font("Avenir Next", FontWeight.BOLD, 12));
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