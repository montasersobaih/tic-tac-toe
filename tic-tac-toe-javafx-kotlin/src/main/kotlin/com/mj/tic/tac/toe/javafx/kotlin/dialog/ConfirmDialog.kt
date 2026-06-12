package com.mj.tic.tac.toe.javafx.kotlin.dialog

import com.mj.tic.tac.toe.javafx.kotlin.constant.DInterface
import com.mj.tic.tac.toe.javafx.kotlin.dialog.ConfirmDialog.Companion.show
import com.mj.tic.tac.toe.javafx.kotlin.dialog.ConfirmDialog.Companion.showAndWait
import com.mj.tic.tac.toe.javafx.kotlin.util.FXMLUtil
import java.net.URL
import java.util.ResourceBundle
import java.util.stream.Stream
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color

/**
 * A confirmation dialog with "Confirm" and "Cancel" buttons.
 *
 * Supports two usage modes:
 * - **Blocking:** [showAndWait] displays the dialog modally and
 *   returns `true` (confirmed) or `false` (canceled).
 * - **Non-blocking:** [show] displays the dialog and invokes
 *   optional [Runnable] callbacks on confirm or cancel.
 *
 * The dialog uses [BaseDialog.showAndWait]'s nested event loop
 * mechanism for modal operation, and the Builder pattern for
 * flexible configuration.
 *
 * @author Montaser Sobaih
 * @version 1.0
 * @since 13-06-2021
 */

class ConfirmDialog : BaseDialog<BorderPane, Boolean> {

    /** Root layout, injected from FXML. */
    @FXML
    private lateinit var rootPane: BorderPane

    /** Dialog title label. */
    @FXML
    private lateinit var dialogTitle: Label

    /** Dialog message label displaying the prompt text. */
    @FXML
    private lateinit var dialogMessage: Label

    /** Optional callback invoked when the user confirms. */
    private var confirm: Runnable? = null

    /** Optional callback invoked when the user cancels. */
    private var cancel: Runnable? = null

    /**
     * Private constructor. Use [Builder] or companion factory methods.
     *
     * Sets the parent overlay background to transparent and the
     * dialog transition type to CENTER.
     *
     * @param container The stack pane serving as the dialog overlay container.
     */
    @Suppress("ConvertSecondaryConstructorToPrimary")
    private constructor(container: StackPane) : super(container) {
        super.setParentBackground(Color.TRANSPARENT)
        super.setTransitionType(DialogTransition.CENTER)
    }

    /**
     * No-op initialization — layout is loaded in [initializeLayout].
     *
     * @param location Unused.
     * @param resources Unused.
     */
    override fun initialize(location: URL, resources: ResourceBundle) {}

    /**
     * Loads the [DInterface.CONFIRM_DIALOG] FXML layout.
     *
     * @return The root [BorderPane] of the dialog layout.
     */
    override fun initializeLayout(): BorderPane {
        return Stream
            .of(DInterface.CONFIRM_DIALOG)
            .map(FXMLUtil::getFXMLLoader)
            .peek { it?.setController(this@ConfirmDialog) }
            .map(FXMLUtil::loadInterface)
            .findFirst()
            .map(BorderPane::class.java::cast)
            .orElse(null)
    }

    /**
     * Overrides the default key handler: ESCAPE triggers [onCancel].
     *
     * @param event The key event.
     */
    override fun onDialogKeyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ESCAPE) {
            onCancel(ActionEvent(event, this))
        }
    }

    /**
     * Handles the cancel action.
     *
     * Updates the dialog value to `false`, closes the dialog, and
     * invokes the cancel callback (if set).
     *
     * @param event The action event.
     */
    @FXML
    private fun onCancel(event: ActionEvent): Unit {
        updateValueAndClose(false)
        cancel?.run()
    }

    /**
     * Handles the confirmation action.
     *
     * Updates the dialog value to `true`, closes the dialog, and
     * invokes the confirmation callback (if set).
     *
     * @param event The action event.
     */
    @FXML
    private fun onConfirm(event: ActionEvent): Unit {
        let { updateValueAndClose(true) }.also { confirm?.run() }
    }

    companion object {
        /**
         * Shows the dialog modally and returns the user's choice.
         *
         * @param container The overlay container.
         * @param message The prompt message to display.
         * @return `true` if confirmed, `false` otherwise.
         */
        fun showAndWait(container: StackPane, message: String): Boolean {
            return builder(container)
                .setMessage(message)
                .build()
                .showAndWait() ?: false
        }

        /**
         * Shows the dialog non-blocking with optional callbacks.
         *
         * @param container The overlay container.
         * @param message The prompt message to display.
         * @param onConfirm Callback invoked on confirmation (optional).
         * @param onCancel Callback invoked on cancellation (optional).
         */
        fun show(container: StackPane, message: String, onConfirm: Runnable?, onCancel: Runnable? = null) {
            builder(container)
                .setMessage(message)
                .setOnConfirmListener(onConfirm)
                .setOnCancelListener(onCancel)
                .buildAndShow()
        }

        /**
         * Returns a new [Builder] for configuring a [ConfirmDialog].
         *
         * @param container The overlay container.
         * @return A new [Builder] instance.
         */
        fun builder(container: StackPane) = Builder(ConfirmDialog(container))
    }

    /**
     * Builder for configuring [ConfirmDialog] instances.
     *
     * Provides a fluent API for setting the message, confirm callback,
     * and cancel callback before building and/or showing the dialog.
     *
     * @property dialog The [ConfirmDialog] instance being configured.
     */
    class Builder(private val dialog: ConfirmDialog) {

        /**
         * Sets the dialog message text.
         *
         * @param message The message string to display.
         * @return This builder for chaining.
         */
        fun setMessage(message: String): Builder {
            dialog.dialogMessage.text = message
            return this
        }

        /**
         * Sets the callback invoked when the user confirms.
         *
         * @param confirmation The runnable to execute on confirm, or null.
         * @return This builder for chaining.
         */
        fun setOnConfirmListener(confirmation: Runnable?): Builder {
            dialog.confirm = confirmation
            return this
        }

        /**
         * Sets the callback invoked when the user cancels.
         *
         * @param cancellation The runnable to execute on cancel, or null.
         * @return This builder for chaining.
         */
        fun setOnCancelListener(cancellation: Runnable?): Builder {
            dialog.cancel = cancellation
            return this
        }

        /**
         * Builds and returns the configured dialog (not yet shown).
         *
         * Call [build] then [show] separately, or use [buildAndShow].
         *
         * @return The configured [ConfirmDialog] instance.
         */
        fun build(): ConfirmDialog = dialog

        /**
         * Builds and shows the dialog immediately.
         */
        fun buildAndShow(): Unit = build().show()
    }
}