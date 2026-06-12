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
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

class ApplicationBarController : BaseController() {

    private val pseudo = PseudoClass.getPseudoClass("reverse")

    @FXML
    private lateinit var applicationBarPane: StackPane

    @FXML
    private lateinit var applicationBarTitle: Label

    private lateinit var stage: Stage

    private var xOffset = 0.0

    private var yOffset = 0.0

    override fun initialize(url: URL, resources: ResourceBundle) {
        Platform.runLater { this.stage = applicationBarPane.scene.window as Stage }
//        applicationBarPane.sceneProperty().addListener { _, _, scene: Scene ->
//            scene.windowProperty().addListener { _, _, window: Window -> stage = window as Stage }
//        }
    }

    fun setTitle(title: String) = title.let(applicationBarTitle::setText)

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

    @FXML
    private fun onBarPressed(event: MouseEvent) {
        if (event.isPrimaryButtonDown) {
            xOffset = event.x
            yOffset = event.y
        }
    }

    @FXML
    private fun onBarDragged(event: MouseEvent) {
        if (event.isPrimaryButtonDown) {
            stage.x = event.screenX - xOffset
            stage.y = event.screenY - yOffset
        }
    }
}