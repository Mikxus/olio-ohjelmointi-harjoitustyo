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
        var view = new LoginView(this);
        stage.setScene(view.createScene());
        stage.setTitle(view.getTitle());
        stage.show();
    }

    public void showDashboard() {
        var view = new DashboardView(this);
        stage.setScene(view.createScene());
        stage.setTitle(view.getTitle());
        stage.show();
    }
}
