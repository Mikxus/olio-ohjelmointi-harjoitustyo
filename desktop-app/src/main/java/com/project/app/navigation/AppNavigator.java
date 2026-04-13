package com.project.app.navigation;

import javafx.stage.Stage;

import com.project.app.views.LoginView;
import com.project.app.views.DashboardView;

// object to have all app's different views 
public class AppNavigator {
    private final Stage stage;

    public AppNavigator(Stage stage) {
        this.stage = stage;
    }

    public void showLogin() {
        stage.setScene(new LoginView(this).createScene());
        stage.show();
    }

    public void showDashboard() {
        stage.setScene(new DashboardView(this).createScene());
        stage.show();
    }
}
