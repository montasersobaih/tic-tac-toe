package com.mj.tic.tac.toe.javafx.java.dialog;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.events.JFXDialogEvent;
import com.mj.tic.tac.toe.javafx.java.constant.DInterface;
import com.mj.tic.tac.toe.javafx.java.util.Difficulty;
import com.mj.tic.tac.toe.javafx.java.util.FXMLUtil;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Dialog used to let the player choose the computer difficulty.
 *
 * <p>The dialog is backed by {@code difficulty_dialog.fxml}. Each difficulty
 * button stores a {@link Difficulty} value in its JavaFX {@code userData};
 * when the user clicks a button, that value is extracted, stored as the dialog
 * result, and passed to the optional selection callback.</p>
 *
 * <p>The dialog result type is {@link Difficulty}, inherited from
 * {@link BaseDialog}. A selected difficulty is stored through
 * {@link #updateValueAndClose(Object)}. If the user closes the dialog with the
 * close button, ESCAPE key, or another no-selection path, the result remains
 * {@code null}.</p>
 *
 * <p>Use {@link #showAndWait(StackPane)} when the caller needs a blocking
 * result. Use {@link #show(StackPane, Consumer)} or {@link #builder(StackPane)}
 * for non-blocking selection flows.</p>
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 13-06-2021
 */
public final class DifficultyDialog extends BaseDialog<BorderPane, Difficulty> {

    /**
     * Root pane loaded from {@code difficulty_dialog.fxml}.
     */
    @FXML
    private BorderPane rootPane;

    /**
     * Localized dialog title label.
     */
    @FXML
    private Label dialogTitle;

    /**
     * Button that closes the dialog without selecting a difficulty.
     */
    @FXML
    private JFXButton closeButton;

    /**
     * Optional callback invoked after the user chooses a difficulty.
     */
    private Consumer<Difficulty> onDifficultyChosen;

    /**
     * Constructs a difficulty dialog over the given container.
     *
     * <p>The dialog uses a transparent parent overlay background and a centered
     * JFoenix transition.</p>
     *
     * @param container the parent {@link StackPane} that hosts the dialog overlay
     */
    private DifficultyDialog(StackPane container) {
        super(container);
        super.setParentBackground(Color.TRANSPARENT);
        super.setTransitionType(DialogTransition.CENTER);
    }

    /**
     * Shows a blocking difficulty selection dialog.
     *
     * <p>This method delegates to {@link BaseDialog#showAndWait()} and returns
     * the selected {@link Difficulty}. If the user closes the dialog without
     * selecting a difficulty, the result is {@code null}.</p>
     *
     * @param container the parent {@link StackPane} for the dialog overlay
     * @return the selected difficulty, or {@code null} if the dialog closed
     * without a selection
     */
    public static Difficulty showAndWait(StackPane container) {
        return getInstance(container).showAndWait();
    }

    /**
     * Shows a non-blocking difficulty selection dialog.
     *
     * <p>The method builds and shows the dialog immediately. When the user
     * selects a difficulty, the selected {@link Difficulty} is stored as the
     * dialog result, the dialog is closed, and the supplied {@code consumer} is
     * invoked on the JavaFX Application Thread. Closing the dialog without a
     * selection does not invoke the consumer.</p>
     *
     * @param container the parent {@link StackPane} for the dialog overlay
     * @param consumer  invoked with the chosen difficulty, or {@code null} if
     *                  no callback is needed
     */
    public static void show(StackPane container, Consumer<Difficulty> consumer) {
        builder(container).setOnDifficultyChosenListener(consumer).buildAndShow();
    }

    public static DifficultyDialog getInstance(StackPane container) {
        return builder(container).build();
    }

    /**
     * Creates a new builder for a difficulty dialog hosted by the given pane.
     *
     * @param container the parent {@link StackPane} that hosts the dialog overlay
     * @return a builder wrapping a new {@code DifficultyDialog}
     */
    public static Builder builder(StackPane container) {
        return new Builder(new DifficultyDialog(container));
    }

    /**
     * Initializes controller state after FXML loading.
     *
     * <p>The close button is wired here because it is injected from FXML. The
     * close action dismisses the dialog without setting a {@link Difficulty}
     * result.</p>
     *
     * @param location  the FXML location, or {@code null} if unavailable
     * @param resources the resource bundle used by the FXML, or {@code null}
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        closeButton.setOnMouseClicked(event -> this.close());
    }

    /**
     * Loads the FXML layout for the difficulty dialog.
     *
     * <p>The loader is created from {@link DInterface#DIFFICULTY_DIALOG}, this
     * dialog instance is registered as the controller, and the loaded root is
     * cast to {@link BorderPane}.</p>
     *
     * @return the loaded dialog root pane, or {@code null} if FXML loading
     * fails
     */
    @Override
    protected BorderPane initializeLayout() {
        return Stream.of(DInterface.DIFFICULTY_DIALOG)
                .map(FXMLUtil::getFXMLLoader)
                .peek(loader -> loader.setController(DifficultyDialog.this))
                .map(FXMLUtil::loadInterface)
                .findFirst()
                .map(BorderPane.class::cast)
                .orElse(null);
    }

    /**
     * Handles a click on one of the difficulty buttons.
     *
     * <p>The handler expects the event source to be a {@link Button} whose
     * {@code userData} is a {@link Difficulty}. That value is stored as the
     * dialog result via {@link BaseDialog#updateValueAndClose(Object)} and then forwarded
     * to the optional selection callback.</p>
     *
     * @param event the action event fired by the selected difficulty button
     */
    @FXML
    private void onDifficultyChosen(ActionEvent event) {
        Difficulty difficulty = Optional.of(event)
                .map(ActionEvent::getSource)
                .map(Button.class::cast)
                .map(Button::getUserData)
                .map(Difficulty.class::cast)
                .orElse(null);

        super.updateValueAndClose(difficulty);
        Optional.ofNullable(onDifficultyChosen).ifPresent(c -> c.accept(difficulty));
    }

    /*==============================================={inner classes}==================================================*/

    /**
     * Builder for configuring and showing a {@link DifficultyDialog}.
     *
     * <p>Use {@link DifficultyDialog#builder(StackPane)} to create a builder,
     * register callbacks, and then call {@link #build()} or
     * {@link #buildAndShow()}.</p>
     */
    public static class Builder {

        /**
         * Dialog instance being configured by this builder.
         */
        private final DifficultyDialog dialog;

        /**
         * Creates a builder around an already constructed dialog.
         *
         * @param dialog the dialog instance to configure
         */
        private Builder(DifficultyDialog dialog) {
            this.dialog = dialog;
        }

        /**
         * Registers a callback to run when the user selects a difficulty.
         *
         * <p>The callback receives the selected {@link Difficulty}. It is not
         * invoked when the dialog is closed without a selection.</p>
         *
         * @param callback callback invoked with the selected difficulty, or
         *                 {@code null}
         * @return this builder for chaining
         */
        public Builder setOnDifficultyChosenListener(Consumer<Difficulty> callback) {
            dialog.onDifficultyChosen = callback;
            return this;
        }

        /**
         * Registers a callback to run after the dialog is closed.
         *
         * <p>The callback is invoked whenever the dialog finishes closing,
         * whether it was closed by a difficulty selection, the close button, the
         * ESCAPE key, or another close path.</p>
         *
         * @param callback action to run after the dialog closes
         * @return this builder for chaining
         */
        public Builder setOnDialogClosed(Runnable callback) {
            dialog.addEventHandler(JFXDialogEvent.CLOSED, event -> callback.run());
            return this;
        }

        /**
         * Returns the configured dialog without showing it.
         *
         * @return the configured {@link DifficultyDialog}
         */
        public DifficultyDialog build() {
            return dialog;
        }

        /**
         * Builds the configured dialog and shows it immediately.
         *
         * <p>This is the convenience path for non-blocking usage. It delegates
         * to {@link #build()} and then calls the inherited
         * {@link com.jfoenix.controls.JFXDialog#show()} method on the result.</p>
         */
        public void buildAndShow() {
            build().show();
        }
    }
}
