package com.mj.tic.tac.toe.javafx.kotlin.controller.layout

import com.jfoenix.controls.JFXButton
import com.mj.tic.tac.toe.javafx.kotlin.controller.BaseController
import java.net.URL
import java.util.ResourceBundle
import javafx.application.Platform
import javafx.css.PseudoClass
import javafx.event.Event
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.stage.WindowEvent

/**
 * Controller for the custom window title bar.
 *
 * This controller manages the application's custom window chrome,
 * replacing the OS-native title bar with a styled bar that provides:
 * - **Window controls:** Hide (minimize), maximize/restore, and close buttons.
 * - **Window dragging:** Mouse-press-and-drag to reposition the undecorated stage.
 * - **Title display:** Application title label for i18n support.
 *
 * The window uses [javafx.stage.StageStyle.TRANSPARENT] (set in [com.mj.tic.tac.toe.javafx.kotlin.StartupApplication]),
 * so this custom title bar is essential for basic window management.
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

class ApplicationBarController : BaseController() {

    /** Pseudo-class "reverse" for toggling the maximize button icon
     * via CSS when the window is maximized vs. restored. */
    private val pseudo = PseudoClass.getPseudoClass("reverse")

    /** Root container of the title bar, injected from FXML. */
    @FXML
    private lateinit var applicationBarPane: StackPane

    /** The window title label, injected from FXML. */
    @FXML
    private lateinit var applicationBarTitle: Label

    /** The parent Stage, resolved lazily after the scene is attached. */
    private lateinit var stage: Stage

    /** X-offset for window dragging, recorded on mouse press. */
    private var xOffset = 0.0

    /** Y-offset for window dragging, recorded on mouse press. */
    private var yOffset = 0.0

    /**
     * Initializes the controller by resolving the parent [Stage] reference.
     *
     * Uses [Platform.runLater] to defer stage resolution until the scene
     * graph is fully attached to a window. The commented-out code shows
     * an alternative approach using property listeners.
     *
     * @param url Unused.
     * @param resources Unused.
     */
    override fun initialize(url: URL, resources: ResourceBundle) {
        Platform.runLater { this.stage = applicationBarPane.scene.window as Stage }
    }

    /**
     * Updates the title bar label text.
     *
     * @param title The new title text to display.
     */
    fun setTitle(title: String) = title.let(applicationBarTitle::setText)

    /**
     * Handles clicks on the window control buttons.
     *
     * Button actions are determined by their FXML-assigned `id`:
     * - `"hide"`: Iconifies (minimizes) the stage.
     * - `"maximize"`: Toggles maximized state and applies/removes
     *   the `"reverse"` pseudo-class for visual feedback.
     * - `"close"`: Closes the stage, calls [Platform.exit], and
     *   fires a [WindowEvent.WINDOW_CLOSE_REQUEST] for cleanup.
     *
     * @param event The mouse event whose source identifies the button.
     */
    @FXML
    private fun onOptionsBarAction(event: MouseEvent) {
        val button = event.source as JFXButton

        when (button.id) {
            "hide" -> stage.isIconified = true
            "maximize" -> {
                stage.isMaximized = !stage.isMaximized
                Platform.runLater { button.pseudoClassStateChanged(pseudo, stage.isMaximized) }
            }
            "close" -> {
                stage.close()
                Platform.exit()
                Event.fireEvent(stage, WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST))
            }
        }
    }

    /**
     * Records the mouse cursor position on primary button press.
     *
     * Stored offsets are used in [onBarDragged] to compute the
     * window's new position relative to the drag start point.
     *
     * @param event The mouse press event containing cursor coordinates.
     */
    @FXML
    private fun onBarPressed(event: MouseEvent) {
        if (event.isPrimaryButtonDown) {
            xOffset = event.x
            yOffset = event.y
        }
    }

    /**
     * Drags the window to a new position as the mouse moves.
     *
     * Computes the new stage position as `screenX - xOffset` and
     * `screenY - yOffset`, creating a smooth drag effect for the
     * undecorated window.
     *
     * @param event The mouse drag event containing screen coordinates.
     */
    @FXML
    private fun onBarDragged(event: MouseEvent) {
        if (event.isPrimaryButtonDown) {
            stage.x = event.screenX - xOffset
            stage.y = event.screenY - yOffset
        }
    }
}