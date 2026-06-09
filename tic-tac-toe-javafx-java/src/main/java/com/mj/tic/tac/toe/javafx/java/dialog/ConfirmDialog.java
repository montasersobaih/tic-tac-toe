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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Confirmation dialog used when the application needs a yes/no decision.
 *
 * <p>The dialog returns a {@link Boolean} result through the
 * {@link BaseDialog#showAndWait()} API inherited from {@link BaseDialog}:
 * {@code true} means the user confirmed, {@code false} means the user
 * canceled, and {@code null} means the dialog was closed without choosing
 * either action.</p>
 *
 * <p>Use {@link #showAndWait(StackPane, String)} for the common blocking case where
 * the caller needs an immediate boolean answer. Use one of the non-blocking
 * {@code show(...)} overloads when the caller wants to continue immediately
 * and react through callbacks.</p>
 *
 * @author Montaser Sobaih
 * @version 1.0
 * @since 13-06-2021
 */
public final class ConfirmDialog extends BaseDialog<BorderPane, Boolean> {

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label dialogTitle;

    @FXML
    private Label dialogMessage;

    /**
     * Optional callback invoked after the user confirms the dialog.
     */
    private Runnable confirm;

    /**
     * Optional callback invoked after the user cancels the dialog.
     */
    private Runnable cancel;

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
     * blocks via {@link #showAndWait()} until the user confirms, cancels, or
     * closes the dialog.</p>
     *
     * <p>A {@code null} result from {@link #showAndWait()} is converted to
     * {@code false}, so callers can treat every non-confirming close path as a
     * declined confirmation.</p>
     *
     * @param container the parent {@link StackPane} for the dialog overlay
     * @param message   the body message to display
     * @return {@code true} if the user confirmed, {@code false} otherwise
     */
    public static boolean showAndWait(StackPane container, String message) {
        Boolean result = builder(container)
                .setMessage(message)
                .build()
                .showAndWait();
        return Optional.ofNullable(result).orElse(false);
    }

    /**
     * Shows a non-blocking confirmation dialog with a confirmation callback.
     *
     * <p>The dialog is displayed asynchronously. When the user clicks the
     * confirm button the supplied {@code onConfirm} callback is invoked. The
     * cancel button closes the dialog with a {@code false} result and does not
     * run a callback in this overload.</p>
     *
     * @param container the parent {@link StackPane} for the dialog overlay
     * @param message   the body message to display
     * @param onConfirm the action to run when the user confirms
     */
    public static void show(StackPane container, String message, Runnable onConfirm) {
        show(container, message, onConfirm, null);
    }

    /**
     * Shows a non-blocking confirmation dialog with confirm and cancel callbacks.
     *
     * <p>The dialog is displayed asynchronously. Confirming the dialog stores a
     * {@code true} result and invokes {@code onConfirm}. Canceling the dialog
     * stores a {@code false} result and invokes {@code onCancel}. Either
     * callback may be {@code null} when no action is required for that path.</p>
     *
     * @param container the parent {@link StackPane} for the dialog overlay
     * @param message   the body message to display
     * @param onConfirm the action to run when the user confirms, or
     *                  {@code null}
     * @param onCancel  the action to run when the user cancels, or
     *                  {@code null}
     */
    public static void show(StackPane container, String message, Runnable onConfirm, Runnable onCancel) {
        builder(container)
                .setMessage(message)
                .setOnConfirmListener(onConfirm)
                .setOnCancelListener(onCancel)
                .buildAndShow();
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
     * Handles key press events when the dialog is focused.
     *
     * <p>Pressing the {@link KeyCode#ESCAPE ESCAPE} key is treated as a
     * cancellation request &mdash; the dialog closes with a value of
     * {@code false} and triggers the cancel callback, if one was registered.
     * All other key events are silently ignored.</p>
     *
     * <p>This implementation converts the key event into an
     * {@link ActionEvent} and delegates to {@link #onCancel(ActionEvent)} to
     * avoid duplicating the close-and-notify logic.</p>
     *
     * @param event the key event that occurred while the dialog has focus
     */
    @Override
    protected void onDialogKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ESCAPE)) {
            this.onCancel(new ActionEvent(event, this));
        }
    }

    /**
     * Handles clicks on cancel button.
     *
     * <p>Closes the dialog with a value of {@code false} and invokes the
     * cancel {@link Runnable} if one was registered via the builder.</p>
     *
     * @param event the action event from the cancel button
     */
    @FXML
    private void onCancel(ActionEvent event) {
        super.updateValueAndClose(false);
        Optional.ofNullable(cancel).ifPresent(Runnable::run);
    }

    /**
     * Handles clicks on confirm button.
     *
     * <p>Closes the dialog with a value of {@code true} and invokes the
     * confirmation {@link Runnable} if one was registered via the builder.</p>
     *
     * @param event the action event from the confirm button
     */
    @FXML
    private void onConfirm(ActionEvent event) {
        super.updateValueAndClose(true);
        Optional.ofNullable(confirm).ifPresent(Runnable::run);
    }

    /*=================================================={Builder}=====================================================*/

    /**
     * A builder for constructing a {@link ConfirmDialog} with a fluent API.
     *
     * <p>Use {@link ConfirmDialog#builder(StackPane)} to obtain an instance,
     * chain configuration calls, and call {@link #build()} to retrieve the
     * configured dialog. Call {@link #buildAndShow()} when the dialog should be
     * displayed immediately without keeping a reference to it.</p>
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
         * Registers a callback to run when the user clicks the confirm button.
         *
         * @param confirmation the action to run on confirmation
         * @return this builder for chaining
         */
        public Builder setOnConfirmListener(Runnable confirmation) {
            dialog.confirm = confirmation;
            return this;
        }

        /**
         * Registers a callback to run when the user clicks the cancel button.
         *
         * @param cancellation the action to run on cancellation
         * @return this builder for chaining
         */
        public Builder setOnCancelListener(Runnable cancellation) {
            dialog.cancel = cancellation;
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

        /**
         * Builds the configured dialog and shows it immediately.
         *
         * <p>This is a convenience method for non-blocking usage. It delegates
         * to {@link #build()} and then calls the inherited
         * {@link com.jfoenix.controls.JFXDialog#show()} method on the
         * resulting dialog instance.</p>
         */
        public void buildAndShow() {
            build().show();
        }
    }
}
