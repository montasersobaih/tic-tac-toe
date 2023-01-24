package com.mj.tic.tac.toe.javafx.java.dialog;

import com.mj.tic.tac.toe.javafx.java.constant.DInterface;
import com.mj.tic.tac.toe.javafx.java.util.FXMLUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Stream;

/**
 * @author Montaser Sobaih
 * @version 1.0
 * @since 13-06-2021
 */

public final class AlertDialog extends BaseDialog<BorderPane> {

    @FXML
    private BorderPane alertPane;

    @FXML
    private Label alertBody;

    private AlertDialog(StackPane container, String body) {
        super(container);
        this.alertBody.setText(body);
        super.setParentBackground(Color.TRANSPARENT);
        super.setTransitionType(DialogTransition.CENTER);
    }

    public static void show(StackPane pane, String message) {
        new AlertDialog(pane, message).show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    protected BorderPane initializeLayout() {
        return Stream.of(DInterface.ALERT_DIALOG)
                .map(FXMLUtil::getFXMLLoader)
                .peek(loader -> loader.setController(AlertDialog.this))
                .map(FXMLUtil::loadInterface)
                .findFirst()
                .map(BorderPane.class::cast)
                .orElse(null);
    }

    @Override
    protected void onDialogKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            this.close();
        } else {
            super.onDialogKeyPressed(event);
        }
    }

    @FXML
    private void onButtonClick(ActionEvent event) {
        this.close();
    }
}