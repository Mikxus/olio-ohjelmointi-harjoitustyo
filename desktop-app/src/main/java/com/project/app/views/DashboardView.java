package com.project.app.views;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import com.project.app.views.AbstractView;
import com.project.app.navigation.AppNavigator;

public class DashboardView extends AbstractView {
    
    public DashboardView(AppNavigator navigator) {
        super(navigator, 900, 600);
    }

    @Override
    protected final Parent build() {
        Button btn = new Button("Dashboard");
        btn.setOnAction(event -> {
            System.out.println("calculating stuff");
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);

        return root;
    }
}

