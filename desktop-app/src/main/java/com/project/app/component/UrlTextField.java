package com.project.app.component;

import com.project.common.Config;

import javafx.animation.PauseTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;

import java.util.function.Consumer;
import java.net.URL;
import java.net.URI;

/**
 *  Simple url input box 
 */
public class UrlTextField extends VBox {
    private final Text headerText;
    private final CustomTextField input;

    private final ReadOnlyBooleanWrapper valid = new ReadOnlyBooleanWrapper(false);
    private final ObjectProperty<Consumer<String>> onValidUrl = new SimpleObjectProperty<>();

    public UrlTextField(String header_name, String hint) {
        this.headerText = new Text(header_name);
        this.input = new CustomTextField();

        setSpacing(6);
        setAlignment(javafx.geometry.Pos.CENTER_LEFT);

    	headerText.getStyleClass().addAll(Styles.TITLE_1);
		input.setPromptText(hint);

        this.getChildren().addAll(headerText, input);

        /* Detect when user is done with inputting the url */
        PauseTransition pause = new PauseTransition(Duration.millis(500));
        input.textProperty().addListener((obs, oldVal, newVal) -> {
            pause.setOnFinished(e -> {
                boolean empty = this.input.textProperty().isEmpty().get();
                isValidUrl(newVal);

                if (empty) {
                    this.input.pseudoClassStateChanged(Styles.STATE_SUCCESS, false);
                    this.input.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                } else {
                    this.input.pseudoClassStateChanged(Styles.STATE_SUCCESS, isValid());
                    this.input.pseudoClassStateChanged(Styles.STATE_DANGER, !isValid());
                }

                // Handle custom callback when we have valid url
                Consumer<String> validCb = onValidUrl.get();
                if (validCb != null && valid.get() == true)
                        validCb.accept(newVal);
            });
            pause.playFromStart();
        });
    }

    public ObjectProperty<Consumer<String>> onValidUrlProperty() {return onValidUrl;}
    public void setOnValidUrl(Consumer<String> cb) {onValidUrl.set(cb);}

    /**
     * Get textfield's textproperty
     * @return
     */
    public StringProperty textProperty() {return this.input.textProperty();}

    /**
     * Get user inputted text
     * @return
     */
    public String getText() {return this.input.getText();}

    /**
     * Get url validity ReadOnlyBooleanProperty
     * Updates automatically as user inputs valid URL
     */
    public ReadOnlyBooleanProperty validProperty() {return this.valid.getReadOnlyProperty();}

    /**
     * Get Url valid status
     * aka is the user inputted url valid
     * @return
     */
    public boolean isValid() {return valid.get();}

    /**
     * Validates that the user input is valid url with https protocol
     * updates valid property
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

		System.out.println("Config allow http: " + Config.allowHTTP());
		/* Allow http if .env allows it */
		if (Config.allowHTTP() == true && "http".compareToIgnoreCase(address.getProtocol()) == 0) {
			System.out.printf("HTTP protocol used: %s, Input url: %s\n",
				address.getProtocol(), value);
			valid.set(true);
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
