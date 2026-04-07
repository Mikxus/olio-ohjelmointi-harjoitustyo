package com.project.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button("Login with Authentik");
        btn.setOnAction(event -> {
            System.out.println("Opening browser for Authentik...");
            // TODO: Add OAuth logic.
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);

        primaryStage.setTitle("My Java App");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}