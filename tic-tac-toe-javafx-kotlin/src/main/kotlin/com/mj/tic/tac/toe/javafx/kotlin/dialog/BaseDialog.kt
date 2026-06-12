package com.mj.tic.tac.toe.javafx.kotlin.dialog

import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.events.JFXDialogEvent
import com.mj.tic.tac.toe.javafx.kotlin.util.ResourceBundleUtil
import java.net.URL
import java.util.ResourceBundle
import javafx.application.Platform
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.Initializable
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color

/**
 * Abstract base class for modal dialogs with a typed return value.
 *
 * Extends [JFXDialog] (JFoenix Material Design dialog) to provide
 * a reusable pattern for dialogs throughout the application. Supports
 * both blocking (via nested JavaFX event loop) and non-blocking usage.
 *
 * **Type parameters:**
 * - [R]: The root layout type of the dialog content (must extend [Pane]).
 * - [V]: The type of the value returned by [showAndWait].
 *
 * Subclasses must implement:
 * - [initializeLayout] to load the FXML content.
 * - [initialize] to set up dialog controls.
 *
 * @param R The root pane type for the dialog layout.
 * @param V The return value type.
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 15-10-2022
 */

abstract class BaseDialog<R : Pane, V> : JFXDialog, Initializable {

    /**
     * Observable property holding the dialog result value.
     *
     * Set by [updateValueAndClose] and read by [showAndWait] after
     * the nested event loop exits.
     */
    private val dialogValue: ObjectProperty<V> = SimpleObjectProperty()

    /**
     * Protected constructor that initializes the dialog layout and event handlers.
     *
     * Sets the dialog container, disables overlay close (user must
     * interact with the buttons), loads the FXML layout via the
     * abstract [initializeLayout] method, and registers keyboard
     * and open-event handlers.
     *
     * @param container The [StackPane] that serves as the dialog's
     *   overlay container.
     */
    @Suppress("ConvertSecondaryConstructorToPrimary")
    protected constructor(container: StackPane?) : super() {
        super.setDialogContainer(container)
        super.setOverlayClose(false)
        super.setContent(@Suppress("LeakingThis") initializeLayout())
        super.setEventHandler(KeyEvent.ANY, ::onDialogKeyPressed)
        super.addEventHandler(JFXDialogEvent.OPENED) { requestFocus() }
    }

    /**
     * Called by JavaFX after FXML loading to initialize dialog controls.
     *
     * Subclasses should set up button handlers, label text, and any
     * other dynamic content here.
     *
     * @param location The FXML location.
     * @param resources The resource bundle for i18n.
     */
    abstract override fun initialize(location: URL, resources: ResourceBundle)

    /**
     * Loads and returns the FXML-based root pane for the dialog.
     *
     * Typically uses [com.mj.tic.tac.toe.javafx.kotlin.util.FXMLUtil]
     * with the appropriate [com.mj.tic.tac.toe.javafx.kotlin.constant.DInterface]
     * constant.
     *
     * @return The root [Pane] loaded from FXML.
     */
    protected abstract fun initializeLayout(): R

    /**
     * Sets the background color of the parent overlay pane.
     *
     * The overlay pane is the semi-transparent backdrop that appears
     * behind the dialog.
     *
     * @param color The color to set, or null for transparent.
     */
    protected fun setParentBackground(@Suppress("SameParameterValue") color: Color?) {
        val pane = content.parent as Pane
        pane.background = Background(BackgroundFill(color, null, null))
    }

    /**
     * Resolves a localized string from the application's resource bundles.
     *
     * @param key The resource bundle key.
     * @return The localized string, or the key itself if not found.
     */
    protected fun getLocalizedText(key: String): String = ResourceBundleUtil.getString(key)

    /**
     * Handles keyboard events for the dialog.
     *
     * Default implementation closes the dialog when the ESCAPE key
     * is pressed. Subclasses may override for custom keyboard handling.
     *
     * @param event The key event.
     */
    protected open fun onDialogKeyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ESCAPE) {
            close()
        }
    }

    /**
     * Returns the current dialog result value without closing.
     *
     * @return The current value, or null if [updateValueAndClose] has
     *   not been called.
     */
    protected fun getDialogValue(): V? = dialogValue.get()

    /**
     * Stores the dialog result value and closes the dialog.
     *
     * Thread-safe: if called from a non-FX thread, the close operation
     * is dispatched via [Platform.runLater].
     *
     * @param value The result value to store.
     */
    protected fun updateValueAndClose(value: V) {
        dialogValue.set(value)
        if (Platform.isFxApplicationThread()) {
            super.close()
        } else {
            Platform.runLater { super.close() }
        }
    }

    /**
     * Shows the dialog modally and blocks until it is closed.
     *
     * Uses [Platform.enterNestedEventLoop] to pause execution while
     * keeping the JavaFX Application Thread responsive. Must be called
     * from the JavaFX Application Thread.
     *
     * @return The dialog result value, or null if no value was set
     *   before closing.
     * @throws IllegalStateException if called from a non-FX thread.
     */
    @Suppress("UNCHECKED_CAST")
    fun showAndWait(): V? {
        require(Platform.isFxApplicationThread()) {
            "showAndWait() must be called from the JavaFX Application Thread."
        }

        val handler = { _: JFXDialogEvent ->
            Platform.exitNestedEventLoop(this@BaseDialog, dialogValue.get())
        }

        super.addEventHandler(JFXDialogEvent.CLOSED, handler)

        try {
            super.show()
            return Platform.enterNestedEventLoop(this) as V?
        } finally {
            super.removeEventHandler(JFXDialogEvent.CLOSED, handler)
        }
    }
}