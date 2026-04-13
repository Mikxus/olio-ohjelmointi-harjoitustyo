package com.project.app.component;

import javafx.animation.PauseTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import atlantafx.base.layout.InputGroup;
import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;

import java.net.URL;
import java.net.URI;

/**
 *  Simple url input box 
 */
public class UrlTextField extends VBox {
    private final Text header_txt;
    private final CustomTextField input;

    private final ReadOnlyBooleanWrapper valid = new ReadOnlyBooleanWrapper(false);

    public UrlTextField(String header_name, String hint) {
        this.header_txt = new Text(header_name);
        this.input = new CustomTextField();

        setSpacing(6);
        setAlignment(javafx.geometry.Pos.CENTER_LEFT);

    	header_txt.getStyleClass().addAll(Styles.TITLE_1);
		input.setPromptText(hint);

        this.getChildren().addAll(header_txt, input);

        /* Detect when user is done with inputting the url */
        PauseTransition pause = new PauseTransition(Duration.millis(500));
        input.textProperty().addListener((obs, oldVal, newVal) -> {
            pause.setOnFinished(e -> isValidUrl(newVal));
            pause.playFromStart();
        });
    }

    /**
     * Get url validity ReadOnlyBooleanProperty
     * Updates automatically as user inputs valid URL
     */
    public ReadOnlyBooleanProperty validProperty() {return valid.getReadOnlyProperty();}

    /**
     * Get Url valid status
     * aka is the user inputted url valid
     * @return
     */
    public boolean isValid() {return valid.get();}

    /**
     * Validates that the user input is valid url with https protocol
     * @param value
     * @return
     */
    private boolean isValidUrl(String value) {
       URL address;
       try {
            address = new URI(value).toURL();
       } catch (Exception e) {
            System.out.printf("Invalid url: %s exception %s\n", value, e);
            valid.set(false);
            return false;
       } 

       // only allow https 
       if ("https".compareToIgnoreCase(address.getProtocol()) != 0) {
            System.out.printf("incorrect protcol used: %s, Input url: %s\n",
                address.getProtocol(), value);
            valid.set(false);
            return false;
       }

       System.out.printf("Valid url inputted: %s\n", value);
       valid.set(true);
       return true;
    }
}
