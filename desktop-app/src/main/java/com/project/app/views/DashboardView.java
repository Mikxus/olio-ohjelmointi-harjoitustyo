package com.project.app.views;

import com.project.app.api.ApiClient;
import com.project.app.auth.AuthFlow;
import com.project.app.navigation.AppNavigator;

/* Concurrency */
import javafx.concurrent.Task;
import javafx.beans.property.*;
import javafx.beans.binding.Bindings;

import com.project.common.api.dto.LahjaCreateRequestObj;
/* lahjat api response data */
import com.project.common.api.dto.LahjaGetObj;
import com.project.common.api.dto.LahjatResponse;
import com.project.common.api.dto.StatusResponse;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
/* UI */
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;


public class DashboardView extends AbstractView {
    
    private final TableView<LahjaGetObj> lahjatTable = new TableView<>();
    private final SimpleObjectProperty<BigDecimal> totalLahjaPrice = new SimpleObjectProperty<>(BigDecimal.ZERO);

    public DashboardView(AppNavigator navigator, ApiClient api, AuthFlow auth) {
        super(navigator, api, auth, 900, 600);
    }

    @Override
    protected final Parent build() {
        var up = createTopPane();
        var seperator = new Separator(Orientation.HORIZONTAL);
        var bottom = createBottomPane("Lahjat");
        VBox.setVgrow(bottom, Priority.ALWAYS);

        var totalHintaHbox = new HBox();
        var hintaLabel = new Label("Lahjojen hinta: ");
        var hinta = new Label();

        hinta.textProperty().bind(Bindings.createStringBinding(
            () -> {
                BigDecimal value = totalLahjaPrice.getValue();
                return value.toString() + " €";
        }, totalLahjaPrice));

        totalHintaHbox.setAlignment(Pos.CENTER);
        totalHintaHbox.getChildren().addAll(hintaLabel, hinta);

        initLahjatTableView();
        refreshLahjatAsync(300);

        VBox root = new VBox(up, seperator, bottom, new Separator(), totalHintaHbox); 
        root.setPadding(new Insets(10, 10,10, 10));
        return root;
    }

    private void updateTotalLahjaPrice() {
        BigDecimal total = BigDecimal.ZERO;

        for (LahjaGetObj data: lahjatTable.getItems()) {
            total = total.add(data.hinta());
        }

        totalLahjaPrice.setValue(total);
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
            updateTotalLahjaPrice();
            System.out.println("refreshLahjatAsync(): Response" + response.lahjat());
        });

        task.setOnFailed(evt -> {
            var error = task.getException();
            System.err.println("refreshLahjatAsync(): Failed reason: " + error.toString());
        });

        var worker = new Thread(task, "lahja-async-fetch");
        worker.setDaemon(true);
        worker.start();
    }

    private void createLahjaRequestAsync(LahjaCreateRequestObj request) {
        Task<StatusResponse> task = new Task<>() {
            @Override
            protected StatusResponse call() {
                return api.createLahja(request);
            }
        };

        task.setOnSucceeded(evt -> {
            refreshLahjatAsync(300);
            System.out.println("createLahjaRequestAsync(): Successfully created new lahja");
        });

        task.setOnFailed(evt -> {
            var error = task.getException();
            System.err.println("createLahjaRequestAsync(): Failed reason: " + error.toString());
        });

        System.out.println("createLahjaRequestAsync(): got Request: " + request);
        var worker = new Thread(task, "laha-async-create");
        worker.setDaemon(true);
        worker.start();
    } 

    private void initLahjatTableView() {
        var id = new TableColumn<LahjaGetObj, Number>("Id");
        id.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().id()));

        var createdAt = new TableColumn<LahjaGetObj, String>("Luotu"); 
        createdAt.setCellValueFactory(cell -> 
            new ReadOnlyStringWrapper(
                cell.getValue().created_at().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"))
            ));

        var lahja = new TableColumn<LahjaGetObj, String>("Lahja");
        lahja.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().lahja()));        

        var hinta = new TableColumn<LahjaGetObj, BigDecimal>("Hinta");
        hinta.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().hinta()));
        
        var valmistaja = new TableColumn<LahjaGetObj, String>("Valmistaja");
        valmistaja.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().valmistaja()));

        var saaja = new TableColumn<LahjaGetObj, String>("saaja");
        saaja.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().saaja()));

        lahjatTable.getColumns().clear();
        lahjatTable.getColumns().add(id);
        lahjatTable.getColumns().add(createdAt);
        lahjatTable.getColumns().add(lahja);
        lahjatTable.getColumns().add(hinta);
        lahjatTable.getColumns().add(valmistaja);
        lahjatTable.getColumns().add(saaja);
        lahjatTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        lahjatTable.getStyleClass().addAll(Styles.STRIPED);
    }

    private Pane createBottomPane(String text) {
        var label = new Label(text);
        label.getStyleClass().addAll(Styles.TITLE_2);
        var root = new VBox(2, label, lahjatTable);

        VBox.setVgrow(lahjatTable, Priority.ALWAYS);
        return root;
    }

    /* atlantafx sampler code */
    private Pane createTopPane() {
        var pane = new StackPane();
        var inputVbox = new VBox();

        pane.getStyleClass().add("bordered");
        pane.setMinSize(200, 165);
        pane.setPrefHeight(180);
        pane.setMaxHeight(180);

        // Lahjan nimi hbox
        var nimiHBox = new HBox(10);
        var nimiLabel = new Label("Nimi");
        var nimiTf = new CustomTextField();
        var nimiSpacer = new Spacer();

        nimiLabel.getStyleClass().addAll(Styles.TITLE_3);
        nimiTf.setPrefWidth(300);
        nimiTf.setPromptText("Nimi");
        HBox.setHgrow(nimiSpacer, Priority.ALWAYS);
        nimiHBox.getChildren().addAll(nimiLabel, nimiSpacer, nimiTf);

        // Lahjan hinta hbox
        var hintaHBox = new HBox(10);
        var hintaLabel = new Label("Hinta");
        var hintaTf = new Spinner<Double>(0.0, 1000.0, 0.0);
        var hintaSpacer = new Pane();

        hintaLabel.getStyleClass().addAll(Styles.TITLE_3);
        hintaTf.setPrefWidth(300);
        hintaTf.setEditable(true);
        HBox.setHgrow(hintaSpacer, Priority.ALWAYS);
        hintaHBox.getChildren().addAll(hintaLabel, hintaSpacer, hintaTf);

        // Valmistaja hbox
        var valmistajaHBox = new HBox(10);
        var valmistajaLabel = new Label("Valmistaja");
        var valmistajaTf = new CustomTextField();
        var valmistajaSpacer = new Pane();

        valmistajaLabel.getStyleClass().addAll(Styles.TITLE_3);
        valmistajaTf.setPrefWidth(300);
        valmistajaTf.setPromptText("Fazer");
        HBox.setHgrow(valmistajaSpacer, Priority.ALWAYS);
        valmistajaHBox.getChildren().addAll(valmistajaLabel, valmistajaSpacer, valmistajaTf);

        // Saaja hbox
        var saajaHBox = new HBox(10);
        var saajaLabel = new Label("Saaja");
        var saajaTf = new CustomTextField();
        var saajaSpacer = new Pane();

        saajaLabel.getStyleClass().addAll(Styles.TITLE_3);
        saajaTf.setPrefWidth(300);
        saajaTf.setPromptText("Mäkisen leipomo");
        HBox.setHgrow(saajaSpacer, Priority.ALWAYS);
        saajaHBox.getChildren().addAll(saajaLabel, saajaSpacer, saajaTf);

        inputVbox.setSpacing(5);
        inputVbox.getChildren().addAll(
            nimiHBox,
            hintaHBox,
            valmistajaHBox,
            saajaHBox
        );

        /** now we have:
         *  [label] -- [text field]
         *  [label] -- [text field]
         *  [label] -- [text field]
         * 
         *  Lets add:
         *  [label] -- [text field] |\\\\\\\\\\\
         *  [label] -- [text field] |   button |
         *  [label] -- [text field] |__________|
         * 
         */
        var rootHbox = new HBox();
        var button = new Button("Luo lahja");
        var inputSeparator = new Separator();
        inputSeparator.setOrientation(Orientation.VERTICAL);

        button.getStyleClass().addAll(Styles.LARGE, Styles.DANGER);
        button.disableProperty().bind(Bindings.createBooleanBinding(
            () -> nimiTf.getText().isBlank()
                || hintaTf.getEditor().getText().isBlank()
                || valmistajaTf.getText().isBlank()
                || saajaTf.getText().isBlank(),
            nimiTf.textProperty(),
            hintaTf.getEditor().textProperty(),
            valmistajaTf.textProperty(),
            saajaTf.textProperty()
        ));

        button.setOnAction(event -> {
            createLahjaRequestAsync(new LahjaCreateRequestObj(
                nimiTf.getText().trim(),
                BigDecimal.valueOf(hintaTf.getValue()),
                valmistajaTf.getText().trim(),
                saajaTf.getText().trim()
            ));

        });

        HBox.setHgrow(inputVbox, Priority.ALWAYS);
        rootHbox.getChildren().addAll(inputVbox, inputSeparator, button);
        pane.getChildren().addAll(rootHbox);
        return pane;
    }
}

