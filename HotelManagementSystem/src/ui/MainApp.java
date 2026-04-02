package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Instantiate our main dashboard layout
        Dashboard dashboard = new Dashboard();
        
        // Create a scene with our layout, width 800, height 600
        Scene scene = new Scene(dashboard.getView(), 800, 600);
        
        primaryStage.setTitle("Hotel Management System");
        primaryStage.setScene(scene);
        primaryStage.show(); // Display the window [cite: 1442]
    }

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application [cite: 1445]
    }
}