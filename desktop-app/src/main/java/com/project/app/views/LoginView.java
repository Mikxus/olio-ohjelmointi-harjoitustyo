package com.project.app.views;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


// Icons
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;

import com.project.app.navigation.AppNavigator;
import com.project.app.component.UrlTextField;;

public class LoginView extends AbstractView {
    
    public LoginView(AppNavigator navigator) {
        super(navigator, 400, 600);
    }

    @Override
    protected final Parent build() {
        var icon = new FontIcon(Material2MZ.VPN_KEY);
        var vbox_layout = new VBox(20);
        var server_url = new UrlTextField("Server address", "https://test.mikxus.dev");

        icon.setStyle("-fx-icon-size: 96;");

        // Layout
        vbox_layout.setPadding(new Insets(30, 20, 0, 20));
        vbox_layout.setAlignment(javafx.geometry.Pos.TOP_CENTER);

        Button btn = new Button("Login with Authentik");
        btn.disableProperty().bind(server_url.validProperty().not());
        btn.setOnAction(event -> {
            System.out.println("Opening browser for Authentik...");
            navigator.showDashboard();
        });

        vbox_layout.getChildren().addAll(icon, server_url, btn);
        return vbox_layout;
    }

    @Override
    public String getTitle() {return "Login";}
}

