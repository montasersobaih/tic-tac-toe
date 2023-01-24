package com.mj.tic.tac.toe.javafx.java.dialog;

import com.mj.tic.tac.toe.javafx.java.constant.DInterface;
import com.mj.tic.tac.toe.javafx.java.util.FXMLUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;

/**
 * @author Montaser Sobaih
 * @version 1.0
 * @since 13-06-2021
 */

public final class ConfirmDialog extends BaseDialog<BorderPane> {

    @FXML
    private BorderPane confirmPane;

    @FXML
    private Label confirmTitle;

    @FXML
    private Label confirmBody;

    private ButtonListener confirm;

    private ButtonListener decline;

    private ConfirmDialog(StackPane container) {
        super(container);
        super.setParentBackground(Color.TRANSPARENT);
        super.setTransitionType(DialogTransition.CENTER);
    }

    public static Builder getInstance(StackPane container) {
        return new Builder(new ConfirmDialog(container));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    protected BorderPane initializeLayout() {
        return Stream.of(DInterface.CONFIRM_DIALOG)
                .map(FXMLUtil::getFXMLLoader)
                .peek(loader -> loader.setController(ConfirmDialog.this))
                .map(FXMLUtil::loadInterface)
                .findFirst()
                .map(BorderPane.class::cast)
                .orElse(null);
    }

    @FXML
    private void onConfirmButton(ActionEvent event) {
        this.close();
        Optional.ofNullable(confirm).ifPresent(ButtonListener::doAction);
    }

    @FXML
    private void onDeclineButton(ActionEvent event) {
        this.close();
        Optional.ofNullable(decline).ifPresent(ButtonListener::doAction);
    }

    /*=================================================={Builder}=====================================================*/
    public static class Builder {

        private final ConfirmDialog dialog;

        private Builder(ConfirmDialog dialog) {
            this.dialog = dialog;
        }

        public Builder setMessage(String message) {
            dialog.confirmBody.setText(message);
            return this;
        }

        public Builder setOnConfirmListener(ButtonListener listener) {
            dialog.confirm = listener;
            return this;
        }

        public Builder setOnDeclineListener(ButtonListener listener) {
            dialog.decline = listener;
            return this;
        }

        public ConfirmDialog build() {
            return dialog;
        }
    }

    /*=================================================={Listener}====================================================*/
    public interface ButtonListener {

        void doAction();
    }
}