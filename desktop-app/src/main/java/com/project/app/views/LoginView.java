package com.project.app.views;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import atlantafx.base.theme.Styles;

/* Icons */
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;

/* UI */
import com.project.app.navigation.AppNavigator;
import com.project.app.component.UrlTextField;

/* Api & Auth */
import com.project.common.api.dto.StatusResponse;
import com.project.app.api.ApiClient;
import com.project.app.auth.AuthFlow;

public class LoginView extends AbstractView {
    public LoginView(AppNavigator navigator, ApiClient api, AuthFlow auth) {
        super(navigator, api, auth, 400, 600);
    }

    @Override
    protected final Parent build() {
        var icon = new FontIcon(Material2MZ.VPN_KEY);
        var vbox_layout = new VBox(20);
        var server_url = new UrlTextField("Server address", "https://test.mikxus.dev");
        Button btn = new Button("Login with Authentik");
        btn.setDisable(true);
        btn.getStyleClass().add(Styles.ACCENT);
        icon.setStyle("-fx-icon-size: 96;");

        // Layout
        vbox_layout.setPadding(new Insets(30, 20, 0, 20));
        vbox_layout.setAlignment(javafx.geometry.Pos.TOP_CENTER);

        /* Listen when user inputs valid url -> try to contact api/status */
        server_url.setOnValidUrl((newVal) -> {
            StatusResponse status;
            api.setBaseUrl(server_url.getText());
            status = api.getStatus();
            
            if (status.status()) {
                btn.getStyleClass().add(Styles.SUCCESS);
            } else {
                btn.getStyleClass().add(Styles.ACCENT);
            }
            
            btn.setDisable(!status.status());
        });

        btn.setOnAction(event -> {
            System.out.println("Opening browser for Authentik...");
            try {
                this.auth.loginAndGetCode();
            } catch (Exception e) {
                System.err.printf("Login failed Exception: %s", e);
                return;
            }

            navigator.showDashboard();
        });

        vbox_layout.getChildren().addAll(icon, server_url, btn);
        return vbox_layout;
    }

    @Override
    public String getTitle() {return "Login";}
}

