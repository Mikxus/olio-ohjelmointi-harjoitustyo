package com.project.app.views;

import com.project.app.api.ApiClient;
import com.project.app.auth.AuthFlow;
import com.project.app.navigation.AppNavigator;

/* Concurrency */
import javafx.concurrent.Task;
import javafx.beans.property.*;

/* lahjat api response data */
import com.project.common.api.dto.LahjaDto;
import com.project.common.api.dto.LahjatResponse;

import atlantafx.base.theme.Styles;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import javafx.collections.FXCollections;
/* UI */
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;



public class DashboardView extends AbstractView {
    
    private final TableView<LahjaDto> lahjatTable = new TableView<>();

    public DashboardView(AppNavigator navigator, ApiClient api, AuthFlow auth) {
        super(navigator, api, auth, 900, 600);
    }

    @Override
    protected final Parent build() {
        var up = createPane("up", Orientation.HORIZONTAL);
        var seperator = new Separator(Orientation.VERTICAL);
        var bottom = createBottomPane("Bottom");

        VBox.setVgrow(bottom, Priority.ALWAYS);

        initLahjatTableView();
        refreshLahjatAsync(20);

        VBox root = new VBox(up, seperator, bottom); 
        return root;
    }

    private void refreshLahjatAsync(int count) {
        Task<LahjatResponse> task = new Task<>() {
            @Override 
            protected LahjatResponse call() {
                return api.getLahjat(count);
            }
        };

        task.setOnSucceeded(evt -> {
            var response = task.getValue();
            lahjatTable.setItems(FXCollections.observableArrayList(response.lahjat()));
            System.out.println("Response" + response.lahjat());
        });

        task.setOnFailed(evt -> {
            var error = task.getException();
            System.err.println("refreshLahjatAsync(): Failed reason: " + error.toString());
        });

        var worker = new Thread(task, "lahja-async-fetch");
        worker.setDaemon(true);
        worker.start();
    }

    private void initLahjatTableView() {
        var id = new TableColumn<LahjaDto, Number>("Id");
        id.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().id()));

        var createdAt = new TableColumn<LahjaDto, String>("Luotu"); 
        createdAt.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().created_at().toString()));

        var lahja = new TableColumn<LahjaDto, String>("Lahja");
        createdAt.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().lahja()));        

        var hinta = new TableColumn<LahjaDto, BigDecimal>("Hinta");
        hinta.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().hinta()));
        
        var valmistaja = new TableColumn<LahjaDto, String>("Valmistaja");
        valmistaja.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().valmistaja()));

        lahjatTable.getColumns().clear();
        lahjatTable.getColumns().add(id);
        lahjatTable.getColumns().add(createdAt);
        lahjatTable.getColumns().add(lahja);
        lahjatTable.getColumns().add(hinta);
        lahjatTable.getColumns().add(valmistaja);
        lahjatTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    private Pane createBottomPane(String text) {
        var label = new Label(text);
        label.getStyleClass().addAll(Styles.TITLE_2);
        var root = new VBox(8, label, lahjatTable);

        VBox.setVgrow(lahjatTable, Priority.ALWAYS);
        return root;
    }
    /* atlantafx sampler code */
    private Pane createPane(String text, Orientation orientation) {
        var pane = new StackPane(new Label(text));
        pane.getStyleClass().add("bordered");
        pane.setMinSize(100, 100);

        if (orientation == Orientation.HORIZONTAL) {
            pane.setPrefHeight(100);
            pane.setMaxHeight(100);
        }

        if (orientation == Orientation.VERTICAL) {
            pane.setPrefWidth(100);
            pane.setMaxWidth(100);
        }

        return pane;
    }
}

