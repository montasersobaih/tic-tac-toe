package com.mj.tic.tac.toe.javafx.java.dialog;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.events.JFXDialogEvent;
import com.mj.tic.tac.toe.javafx.java.util.ResourceBundleUtil;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 15-10-2022
 */

public abstract class BaseDialog<R extends Pane> extends JFXDialog implements Initializable {

    protected BaseDialog(StackPane container) {
        super.setDialogContainer(container);
        super.setOverlayClose(false);
        super.setContent(initializeLayout());
        super.setEventHandler(KeyEvent.ANY, this::onDialogKeyPressed);
        super.addEventHandler(JFXDialogEvent.OPENED, event -> requestFocus());
    }

    @Override
    public abstract void initialize(URL location, ResourceBundle resources);

    protected abstract R initializeLayout();

    protected void setParentBackground(Color color) {
        Pane pane = (Pane) getContent().getParent();

        Background background = new Background(new BackgroundFill(color, null, null));
        pane.setBackground(background);
    }

    protected final String getString(String key) {
        return ResourceBundleUtil.getString(key);
    }

    protected void onDialogKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ESCAPE)) {
            this.close();
        }
    }
}
