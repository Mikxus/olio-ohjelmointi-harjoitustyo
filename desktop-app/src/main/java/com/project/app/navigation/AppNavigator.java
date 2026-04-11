package com.project.app.navigation;
import javafx.stage.Stage;

// object to have all app's different views 
public class AppNavigator {
    private final Stage stage;

    public AppNavigator(Stage stage) {
        this.stage = stage;
    }

    public void showLogin() {stage.setScene();}
}
