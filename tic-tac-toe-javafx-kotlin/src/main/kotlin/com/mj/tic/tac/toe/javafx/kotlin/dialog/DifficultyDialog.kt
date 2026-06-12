package com.mj.tic.tac.toe.javafx.kotlin.dialog

import com.jfoenix.controls.JFXButton
import com.mj.tic.tac.toe.javafx.kotlin.constant.DInterface
import com.mj.tic.tac.toe.javafx.kotlin.dialog.DifficultyDialog.Companion.show
import com.mj.tic.tac.toe.javafx.kotlin.dialog.DifficultyDialog.Companion.showAndWait
import com.mj.tic.tac.toe.javafx.kotlin.util.Difficulty
import com.mj.tic.tac.toe.javafx.kotlin.util.FXMLUtil
import java.net.URL
import java.util.ResourceBundle
import java.util.function.Consumer
import java.util.stream.Stream
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color

/**
 * Dialog for selecting the AI difficulty level.
 *
 * Presents the player with three difficulty choices (Easy, Medium, Hard)
 * as buttons. Each button stores its [Difficulty] value in `userData`,
 * which is read by [onDifficultyChosen] when clicked.
 *
 * Supports two usage modes:
 * - **Blocking:** [showAndWait] returns the chosen [Difficulty] or null
 *   if the dialog is dismissed.
 * - **Non-blocking:** [show] displays the dialog with an optional
 *   [Consumer] callback for the selected difficulty.
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

class DifficultyDialog private constructor(container: StackPane) : BaseDialog<BorderPane, Difficulty>(container) {

    /** Root layout, injected from FXML. */
    @FXML
    private lateinit var rootPane: BorderPane

    /** Dialog title label. */
    @FXML
    private lateinit var dialogTitle: Label

    /** Close button (X) in the title bar. */
    @FXML
    private lateinit var closeButton: JFXButton

    /** Optional callback invoked when a difficulty is selected. */
    private var onDifficultyChosen: Consumer<Difficulty>? = null

    init {
        setParentBackground(Color.TRANSPARENT)
        transitionType = DialogTransition.CENTER
    }

    /**
     * Initializes the controller: sets up the close button handler.
     *
     * @param location Unused.
     * @param resources Unused.
     */
    override fun initialize(location: URL, resources: ResourceBundle) {
        closeButton.setOnMouseClicked { close() }
    }

    /**
     * Loads the [DInterface.DIFFICULTY_DIALOG] FXML layout.
     *
     * @return The root [BorderPane] of the dialog layout.
     */
    override fun initializeLayout(): BorderPane {
        return Stream
            .of(DInterface.DIFFICULTY_DIALOG)
            .map(FXMLUtil::getFXMLLoader)
            .peek { it?.setController(this@DifficultyDialog) }
            .map(FXMLUtil::loadInterface)
            .findFirst()
            .map(BorderPane::class.java::cast)
            .orElse(null)
    }

    /**
     * Handles a difficulty button click.
     *
     * Extracts the [Difficulty] from the clicked button's `userData`,
     * stores it as the dialog result via [updateValueAndClose], and
     * invokes the [onDifficultyChosen] callback (if set).
     *
     * @param event The action event whose source is the clicked button.
     */
    @FXML
    private fun onDifficultyChosen(event: ActionEvent) {
        val difficulty = (event.source as Button).userData as? Difficulty
        if (difficulty != null) {
            updateValueAndClose(difficulty)
            onDifficultyChosen?.accept(difficulty)
        }
    }

    companion object {
        /**
         * Shows the difficulty dialog modally and returns the choice.
         *
         * @param container The overlay container.
         * @return The selected [Difficulty], or null if dismissed.
         */
        fun showAndWait(container: StackPane): Difficulty? = getInstance(container).showAndWait()

        /**
         * Shows the difficulty dialog non-blocking with a result consumer.
         *
         * @param container The overlay container.
         * @param consumer Callback invoked with the selected difficulty.
         */
        fun show(container: StackPane, consumer: Consumer<Difficulty>?): Unit {
            builder(container).setOnDifficultyChosenListener(consumer).buildAndShow()
        }

        /**
         * Returns a builder-constructed [DifficultyDialog] instance.
         *
         * @param container The overlay container.
         * @return A new [DifficultyDialog] instance.
         */
        fun getInstance(container: StackPane): DifficultyDialog = builder(container).build()

        /**
         * Returns a new [Builder] for configuring a [DifficultyDialog].
         *
         * @param container The overlay container.
         * @return A new [Builder] instance.
         */
        fun builder(container: StackPane) = Builder(DifficultyDialog(container))
    }

    /**
     * Builder for configuring [DifficultyDialog] instances.
     *
     * Provides a fluent API for setting the difficulty-chosen callback
     * and dialog-closed event handler.
     *
     * @property dialog The [DifficultyDialog] instance being configured.
     */
    class Builder(private val dialog: DifficultyDialog) {

        /**
         * Sets the callback invoked when a difficulty is selected.
         *
         * @param callback The consumer to accept the chosen difficulty, or null.
         * @return This builder for chaining.
         */
        fun setOnDifficultyChosenListener(callback: Consumer<Difficulty>?): Builder {
            dialog.onDifficultyChosen = callback
            return this
        }

        /**
         * Registers a callback for the dialog-closed event.
         *
         * @param callback The runnable to execute when the dialog closes.
         * @return This builder for chaining.
         */
        fun setOnDialogClosed(callback: Runnable): Builder {
            dialog.addEventHandler(com.jfoenix.controls.events.JFXDialogEvent.CLOSED) { callback.run() }
            return this
        }

        /**
         * Builds and returns the configured dialog (not yet shown).
         *
         * @return The configured [DifficultyDialog] instance.
         */
        fun build(): DifficultyDialog = dialog

        /**
         * Builds and shows the dialog immediately.
         */
        fun buildAndShow(): Unit = build().show()
    }
}
