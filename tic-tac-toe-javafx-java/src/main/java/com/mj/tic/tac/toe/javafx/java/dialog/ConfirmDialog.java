package com.mj.tic.tac.toe.javafx.java.dialog;

import com.mj.tic.tac.toe.javafx.java.constant.DInterface;
import com.mj.tic.tac.toe.javafx.java.util.FXMLUtil;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * A modal confirmation dialog with confirm and decline actions.
 *
 * <p>Displayed as an overlay on a {@link StackPane} container, this dialog
 * presents a title, a body message, and two action buttons (confirm /
 * decline). The calling code registers callbacks via the {@link Builder}
 * to be notified when the user clicks either button.</p>
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 * ConfirmDialog.getInstance(container)
 *     .setMessage("Are you sure you want to restart?")
 *     .setOnConfirmListener(() -> resetGame())
 *     .setOnDeclineListener(() -> closeDialog())
 *     .build()
 *     .show();
 * }</pre>
 *
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

    /**
     * Creates a new {@link Builder} for constructing a {@code ConfirmDialog}
     * over the given container pane.
     *
     * @param container the parent {@link StackPane} that hosts the dialog overlay.
     * @return a new {@link Builder} instance.
     */
    public static Builder getInstance(StackPane container) {
        return new Builder(new ConfirmDialog(container));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

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

    /**
     * Handles clicks on the confirm button: closes the dialog and invokes
     * the confirm listener (if registered).
     */
    @FXML
    private void onConfirmButton(ActionEvent event) {
        this.close();
        Optional.ofNullable(confirm).ifPresent(ButtonListener::doAction);
    }

    /**
     * Handles clicks on the decline button: closes the dialog and invokes
     * the decline listener (if registered).
     */
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

        /**
         * Sets the body message displayed in the confirmation dialog.
         *
         * @param message the message text.
         * @return this builder for chaining.
         */
        public Builder setMessage(String message) {
            dialog.confirmBody.setText(message);
            return this;
        }

        /**
         * Registers a listener to be invoked when the user
         * clicks the confirm (accept) button.
         *
         * @param listener the action to run on confirmation.
         * @return this builder for chaining.
         */
        public Builder setOnConfirmListener(ButtonListener listener) {
            dialog.confirm = listener;
            return this;
        }

        /**
         * Registers a listener to be invoked when the user
         * clicks the decline (cancel) button.
         *
         * @param listener the action to run on decline.
         * @return this builder for chaining.
         */
        public Builder setOnDeclineListener(ButtonListener listener) {
            dialog.decline = listener;
            return this;
        }

        /**
         * Returns the fully-configured {@link ConfirmDialog}.
         *
         * @return the built dialog instance.
         */
        public ConfirmDialog build() {
            return dialog;
        }
    }

    /*=================================================={Listener}====================================================*/

    /**
     * Functional interface for dialog action callbacks.
     *
     * <p>Used by {@link ConfirmDialog} to notify listeners when the user
     * confirms or declines. Unlike {@link Runnable}, this is a dedicated
     * type that makes the intent explicit at the call site.</p>
     */
    public interface ButtonListener {

        /**
         * Performs the action associated with the button click.
         */
        void doAction();
    }
}