package com.project.app.navigation;

import javafx.stage.Stage;

/* UI views */
import com.project.app.views.LoginView;
import com.project.common.Config;
import com.project.app.views.DashboardView;

/* Api & Auth */
import com.project.app.api.ApiClient;
import com.project.app.auth.AuthFlow;

// object to have all app's different views 
public class AppNavigator {
    private final Stage stage;
    private final AuthFlow auth = new AuthFlow();
    private final ApiClient api = new ApiClient(Config.getBackendUrl(), auth);


    public AppNavigator(Stage stage) {
        this.stage = stage;
    }

    public void showLogin() {
        if (auth.isAuthorized() == true && Config.isBackendUrl() == true) {
            api.setBackendUrl(Config.getBackendUrl());
            showDashboard();
            return;
        }

        var view = new LoginView(this, this.api, this.auth);
        stage.setScene(view.createScene());
        stage.setTitle(view.getTitle());
        stage.show();
    }

    public void showDashboard() {
        var view = new DashboardView(this, this.api, this.auth);
        stage.setScene(view.createScene());
        stage.setTitle(view.getTitle());
        stage.show();
    }
}
