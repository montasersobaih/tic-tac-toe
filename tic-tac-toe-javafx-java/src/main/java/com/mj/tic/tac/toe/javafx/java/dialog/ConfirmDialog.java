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
 * @author Montaser Sobaih
 * @version 1.0
 * @since 13-06-2021
 */
public final class ConfirmDialog extends BaseDialog<BorderPane> {

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label dialogTitle;

    @FXML
    private Label dialogMessage;

    private ButtonListener confirm;

    private ButtonListener decline;

    /**
     * Constructs a confirmation dialog over the given container.
     *
     * <p>Sets the overlay background to transparent and uses a
     * {@link DialogTransition#CENTER CENTER} transition.</p>
     *
     * @param container the parent {@link StackPane} that hosts the dialog overlay
     */
    private ConfirmDialog(StackPane container) {
        super(container);
        super.setParentBackground(Color.TRANSPARENT);
        super.setTransitionType(DialogTransition.CENTER);
    }

    /**
     * Shows a blocking confirmation dialog and returns the user's choice.
     *
     * <p>This is a convenience shorthand for the common case where only a
     * message is needed and the caller wants a boolean result. The dialog
     * blocks via {@link #showAndWait()}.</p>
     *
     * @param container the parent {@link StackPane} for the dialog overlay
     * @param message   the body message to display
     * @return {@code true} if the user confirmed, {@code false} otherwise
     */
    public static boolean show(StackPane container, String message) {
        Boolean result = builder(container)
                .setMessage(message)
                .build()
                .showAndWait();
        return Optional.ofNullable(result).orElse(false);
    }

    /**
     * Shows a non-blocking confirmation dialog with a confirm listener.
     *
     * <p>The dialog is displayed asynchronously. When the user clicks the
     * confirm button the supplied {@code listener} is invoked; the decline
     * button simply closes the dialog.</p>
     *
     * @param container the parent {@link StackPane} for the dialog overlay
     * @param message   the body message to display
     * @param listener  the action to run when the user confirms
     */
    public static void show(StackPane container, String message, ButtonListener listener) {
        builder(container)
                .setMessage(message)
                .setOnConfirmListener(listener)
                .build()
                .show();
    }

    /**
     * Creates a new {@link Builder} for constructing a {@code ConfirmDialog}
     * over the given container pane.
     *
     * @param container the parent {@link StackPane} that hosts the dialog overlay
     * @return a new {@link Builder} instance
     */
    public static Builder builder(StackPane container) {
        return new Builder(new ConfirmDialog(container));
    }

    /**
     * No-op initializer; the dialog configuration is performed entirely
     * through the {@link Builder}.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    /**
     * Loads the FXML layout for this dialog.
     *
     * <p>Looks up the FXML resource from {@link DInterface#CONFIRM_DIALOG},
     * creates an {@link javafx.fxml.FXMLLoader} via
     * {@link com.mj.tic.tac.toe.javafx.java.util.FXMLUtil}, sets this
     * instance as the controller, and returns the parsed root
     * {@link BorderPane}.</p>
     *
     * @return the root {@link BorderPane} loaded from FXML, or {@code null}
     * if loading failed
     */
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
     * Handles clicks on cancel button.
     *
     * <p>Closes the dialog with a value of {@code false} and invokes the
     * decline {@link ButtonListener} if one was registered via the builder.</p>
     *
     * @param event the action event from the decline button
     */
    @FXML
    private void onCancel(ActionEvent event) {
        super.updateValueAndClose(false);
        Optional.ofNullable(decline).ifPresent(ButtonListener::doAction);
    }

    /**
     * Handles clicks on confirm button.
     *
     * <p>Closes the dialog with a value of {@code true} and invokes the
     * confirm {@link ButtonListener} if one was registered via the builder.</p>
     *
     * @param event the action event from the confirm button
     */
    @FXML
    private void onConfirm(ActionEvent event) {
        super.updateValueAndClose(true);
        Optional.ofNullable(confirm).ifPresent(ButtonListener::doAction);
    }

    /*=================================================={Builder}=====================================================*/

    /**
     * A builder for constructing a {@link ConfirmDialog} with a fluent API.
     *
     * <p>Use {@link ConfirmDialog#builder(StackPane)} to obtain an instance,
     * chain configuration calls, and call {@link #build()} to retrieve the
     * configured dialog.</p>
     */
    public static class Builder {

        private final ConfirmDialog dialog;

        private Builder(ConfirmDialog dialog) {
            this.dialog = dialog;
        }

        /**
         * Sets the body message displayed in the confirmation dialog.
         *
         * @param message the message text
         * @return this builder for chaining
         */
        public Builder setMessage(String message) {
            dialog.dialogMessage.setText(message);
            return this;
        }

        /**
         * Registers a listener to be invoked when the user clicks confirm button.
         *
         * @param listener the action to run on confirmation
         * @return this builder for chaining
         */
        public Builder setOnConfirmListener(ButtonListener listener) {
            dialog.confirm = listener;
            return this;
        }

        /**
         * Registers a listener to be invoked when the user clicks cancel button.
         *
         * @param listener the action to run on decline
         * @return this builder for chaining
         */
        public Builder setOnDeclineListener(ButtonListener listener) {
            dialog.decline = listener;
            return this;
        }

        /**
         * Returns the fully-configured {@link ConfirmDialog}.
         *
         * @return the built dialog instance
         */
        public ConfirmDialog build() {
            return dialog;
        }
    }

    /*================================================{Inner classes}=================================================*/

    /**
     * Functional interface for dialog action callbacks.
     *
     * <p>Used by {@link ConfirmDialog} to notify listeners when the user
     * confirms or declines. Unlike {@link Runnable}, this is a dedicated
     * type that makes the intent explicit at the call site and improves
     * readability of builder chains.</p>
     */
    public interface ButtonListener {

        /**
         * Performs the action associated with the button click.
         */
        void doAction();
    }
}