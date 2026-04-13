package com.project.app;

import com.project.app.navigation.AppNavigator;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        AppNavigator nav = new AppNavigator(primaryStage); 

        nav.showLogin();
        System.out.println("After showLogin");
    }

    public static void main(String[] args) {
        launch(args);
    }
}