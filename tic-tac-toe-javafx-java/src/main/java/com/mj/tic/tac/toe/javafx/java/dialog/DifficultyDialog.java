package com.mj.tic.tac.toe.javafx.java.dialog;

import com.jfoenix.controls.JFXButton;
import com.mj.tic.tac.toe.javafx.java.constant.DInterface;
import com.mj.tic.tac.toe.javafx.java.util.Difficulty;
import com.mj.tic.tac.toe.javafx.java.util.FXMLUtil;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * A modal dialog that presents the player with a choice of difficulty levels
 * (Easy, Medium, Hard) before starting a new game.
 *
 * <p>The dialog is displayed as an overlay on top of the provided
 * {@link StackPane} container. It supports two interaction models:</p>
 *
 * <ul>
 *   <li><b>Callback (async):</b> Register a listener via
 *       {@link Builder#setOnDifficultyChosenListener(Consumer)} and call
 *       {@link #show()} — the listener receives the chosen
 *       {@link Difficulty} when the user clicks a difficulty button.</li>
 *   <li><b>Blocking (sync):</b> Call {@link #showAndWait()} to block the
 *       calling thread (via a JavaFX nested event loop) until the user makes
 *       a selection, then receive the {@link Difficulty} directly.</li>
 * </ul>
 *
 * <p>The dialog is built through its {@link Builder}, which is obtained via
 * {@link #getInstance(StackPane)}:
 * <pre>{@code
 * DifficultyDialog.getInstance(container)
 *     .setOnDifficultyChosenListener(difficulty -> { ... })
 *     .build()
 *     .show();
 * }</pre>
 *
 * <p>For the common case where no callback is needed, use the static
 * convenience method {@link #showAndWait(StackPane)}:
 * <pre>{@code
 * Difficulty difficulty = DifficultyDialog.showAndWait(container);
 * }</pre>
 *
 * @author Montaser Sobaih
 * @version 1.0
 * @since 13-06-2021
 */
public final class DifficultyDialog extends BaseDialog<BorderPane> {

    /**
     * The root layout loaded from the FXML resource. Houses the dialog's title
     * label and the three difficulty choice buttons.
     */
    @FXML
    private BorderPane rootPane;

    /**
     * The label that displays the dialog's heading text (e.g. "Choose Difficulty").
     * Bound via the FXML file's {@code fx:id}.
     */
    @FXML
    private Label title;

    /**
     * The close button that dismisses the dialog. Injected by the FXMLLoader
     * via the {@code fx:id} attribute in the FXML file.
     */
    @FXML
    private JFXButton closeButton;

    /**
     * An optional callback invoked when the user selects a difficulty level.
     * Set via {@link Builder#setOnDifficultyChosenListener(Consumer)}. If no
     * listener is registered, the selected difficulty is still returned through
     * the blocking {@link #showAndWait()} path via the nested event loop.
     */
    private Consumer<Difficulty> consumer;

    /**
     * Constructs a new difficulty-selection dialog over the given container.
     * <p>
     * The dialog's parent overlay background is set to transparent and the
     * transition type is {@link DialogTransition#CENTER} (scale-in animation).
     * </p>
     *
     * @param container the parent {@link StackPane} that hosts the dialog overlay.
     */
    private DifficultyDialog(StackPane container) {
        super(container);
        super.setParentBackground(Color.TRANSPARENT);
        super.setTransitionType(DialogTransition.CENTER);
    }

    /**
     * Creates a new {@link Builder} for constructing a {@code DifficultyDialog}
     * over the given container pane.
     *
     * @param container the parent {@link StackPane} that hosts the dialog overlay.
     * @return a new {@link Builder} instance.
     */
    public static Builder getInstance(StackPane container) {
        return new Builder(new DifficultyDialog(container));
    }

    /**
     * Static convenience method that creates, shows, and waits for the
     * difficulty dialog in a single call.
     *
     * <p>Equivalent to:
     * <pre>{@code
     * DifficultyDialog.getInstance(container).build().showAndWait()
     * }</pre>
     *
     * @param container the parent {@link StackPane} that hosts the dialog overlay.
     * @return the chosen {@link Difficulty}, or {@code null} if the dialog
     * was closed without a selection.
     */
    public static Difficulty showAndWait(StackPane container) {
        return getInstance(container).build().showAndWait();
    }

    /**
     * Initializes the dialog after its FXML content has been loaded.
     * <p>
     * Wires the close button (injected via {@code fx:id="closeButton"}) to
     * dismiss the dialog when clicked.
     * </p>
     *
     * @param location  the location used to resolve relative paths for the root
     *                  object, or {@code null} if not available.
     * @param resources the resources used to localize the root object, or
     *                  {@code null} if not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        closeButton.setOnMouseClicked(event -> this.close());
    }

    /**
     * Loads the difficulty-dialog layout from its FXML resource and returns
     * the root {@link BorderPane}.
     * <p>
     * This method is called from the {@link BaseDialog} super-constructor, so
     * no instance state may be relied upon beyond what was established before
     * {@code super(container)} completed. The controller is explicitly set on
     * the FXML loader to bypass any {@code fx:controller} attribute in the
     * FXML file, ensuring this instance handles all UI events.
     * </p>
     *
     * @return the root {@link BorderPane} loaded from
     * {@link DInterface#DIFFICULTY_DIALOG}, or {@code null} if loading
     * failed.
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
     * Shows the dialog and blocks the calling thread (via a JavaFX nested
     * event loop) until a difficulty is chosen or the dialog is dismissed.
     *
     * <p>This method must be called from the JavaFX Application Thread.
     * Unlike the callback-based {@link #show()}, it returns the selected
     * {@link Difficulty} directly.</p>
     *
     * @return the chosen {@link Difficulty}, or {@code null} if the dialog
     * was closed without a selection.
     */
    public Difficulty showAndWait() {
        super.show();
        return (Difficulty) Platform.enterNestedEventLoop(this);
    }

    /**
     * Handles clicks on any of the three difficulty buttons (Easy / Medium / Hard).
     * <p>
     * Closes the dialog, extracts the chosen {@link Difficulty} from the
     * clicked button's {@link Button#getUserData() user-data}, then:
     * </p>
     * <ol>
     *   <li>Invokes the optional callback consumer, if one was registered via
     *       the builder.</li>
     *   <li>Exits the nested event loop (unblocking {@link #showAndWait()})
     *       with the selected difficulty as the result.</li>
     * </ol>
     * <p>
     * Each difficulty button in the FXML is expected to have its
     * {@code userData} attribute set to the corresponding {@link Difficulty}
     * enum constant.
     * </p>
     *
     * @param event the action event fired by the clicked {@link Button}.
     */
    @FXML
    private void onDifficultyChosen(ActionEvent event) {
        this.close();

        Difficulty difficulty = Optional.of(event)
                .map(ActionEvent::getSource)
                .map(Button.class::cast)
                .map(Button::getUserData)
                .map(Difficulty.class::cast)
                .orElse(null);

        Optional.ofNullable(consumer).ifPresent(c -> c.accept(difficulty));
        Platform.exitNestedEventLoop(this, difficulty);
    }

    /*==============================================={inner classes}==================================================*/

    /**
     * Builder for constructing a configured {@link DifficultyDialog} instance.
     * <p>
     * Obtain a builder via {@link #getInstance(StackPane)}, optionally register
     * a callback with {@link #setOnDifficultyChosenListener(Consumer)}, and
     * call {@link #build()} to retrieve the fully configured dialog.
     * </p>
     *
     * <pre>{@code
     * DifficultyDialog.getInstance(container)
     *     .setOnDifficultyChosenListener(difficulty -> { ... })
     *     .build()
     *     .show();
     * }</pre>
     */
    public static class Builder {

        /**
         * The dialog instance being configured.
         */
        private final DifficultyDialog dialog;

        /**
         * Creates a new builder wrapping the given dialog.
         *
         * @param dialog the (not yet configured) dialog instance to build upon.
         */
        private Builder(DifficultyDialog dialog) {
            this.dialog = dialog;
        }

        /**
         * Registers a callback to receive the chosen {@link Difficulty} when
         * the user clicks a difficulty button.
         *
         * @param listener the consumer to invoke with the selected difficulty.
         * @return this builder for chaining.
         */
        public Builder setOnDifficultyChosenListener(Consumer<Difficulty> listener) {
            dialog.consumer = listener;
            return this;
        }

        /**
         * Returns the fully-configured {@link DifficultyDialog}.
         *
         * @return the built dialog instance.
         */
        public DifficultyDialog build() {
            return dialog;
        }
    }
}