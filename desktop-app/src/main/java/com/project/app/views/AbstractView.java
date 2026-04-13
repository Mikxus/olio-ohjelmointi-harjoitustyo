package com.project.app.views;

import javafx.scene.Parent;
import javafx.scene.Scene;
import com.project.app.navigation.AppNavigator;

/* 
 * Abstract class to define what Views should have
 * Each view/page "builds" its own page & then returns the root node of the page
 * 
 */
public abstract class AbstractView {
    protected final AppNavigator navigator;
    
    /* Window size per view */
    protected int width, height;
    
    protected AbstractView(AppNavigator navigator, int width, int height) {
        this.navigator = navigator;
        this.width = width;
        this.height = height;
    }

    protected AbstractView(AppNavigator navigator) {
        this.navigator = navigator;
        this.width = 640;
        this.height = 360;
    }

    // Each page provides its root ui node
    protected abstract Parent build();

    public Scene createScene() {
        return new Scene(build(), this.width, this.height);
    }
}