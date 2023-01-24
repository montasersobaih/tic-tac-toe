package com.mj.tic.tac.toe.javafx.java.controller.layout;

import com.jfoenix.controls.JFXButton;
import com.mj.tic.tac.toe.javafx.java.controller.BaseController;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

public final class ApplicationBarController extends BaseController {

    private final PseudoClass pseudo = PseudoClass.getPseudoClass("reverse");

    @FXML
    private StackPane applicationBarPane;

    @FXML
    private Label applicationBarTitle;

    private Stage stage;

    private double xOffset;

    private double yOffset;

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        applicationBarPane.sceneProperty().addListener((i1, i2, scene) -> {
            scene.windowProperty().addListener((i3, i4, window) -> {
                ApplicationBarController.this.stage = (Stage) window;
            });
        });
    }

    public void setTitle(String title) {
        Optional.ofNullable(title).ifPresent(applicationBarTitle::setText);
    }

    @FXML
    private void onOptionsBarAction(MouseEvent event) {
        JFXButton button = (JFXButton) event.getSource();

        switch (button.getId()) {
            case "hide":
                stage.setIconified(true);
                break;
            case "maximize":
                stage.setMaximized(!stage.isMaximized());
                Platform.runLater(() -> button.pseudoClassStateChanged(pseudo, stage.isMaximized()));
                break;
            case "close":
                stage.close();
                Platform.exit();
                Event.fireEvent(stage, new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
                break;
        }
    }

    @FXML
    private void onBarPressed(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            xOffset = event.getX();
            yOffset = event.getY();
        }
    }

    @FXML
    private void onBarDragged(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        }
    }
}
