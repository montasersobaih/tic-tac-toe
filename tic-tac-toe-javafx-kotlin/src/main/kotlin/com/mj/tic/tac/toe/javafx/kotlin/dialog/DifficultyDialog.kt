package com.mj.tic.tac.toe.javafx.kotlin.dialog

import com.jfoenix.controls.JFXButton
import com.mj.tic.tac.toe.javafx.kotlin.constant.DInterface
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
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

class DifficultyDialog private constructor(container: StackPane) : BaseDialog<BorderPane, Difficulty>(container) {

    @FXML
    private lateinit var rootPane: BorderPane

    @FXML
    private lateinit var dialogTitle: Label

    @FXML
    private lateinit var closeButton: JFXButton

    private var onDifficultyChosen: Consumer<Difficulty>? = null

    init {
        setParentBackground(Color.TRANSPARENT)
        transitionType = DialogTransition.CENTER
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        closeButton.setOnMouseClicked { close() }
    }

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

    @FXML
    private fun onDifficultyChosen(event: ActionEvent) {
        val difficulty = (event.source as Button).userData as? Difficulty
        if (difficulty != null) {
            updateValueAndClose(difficulty)
            onDifficultyChosen?.accept(difficulty)
        }
    }

    companion object {
        fun showAndWait(container: StackPane): Difficulty? = getInstance(container).showAndWait()

        fun show(container: StackPane, consumer: Consumer<Difficulty>?): Unit {
            builder(container).setOnDifficultyChosenListener(consumer).buildAndShow()
        }

        fun getInstance(container: StackPane): DifficultyDialog = builder(container).build()

        fun builder(container: StackPane) = Builder(DifficultyDialog(container))
    }

    class Builder(private val dialog: DifficultyDialog) {

        fun setOnDifficultyChosenListener(callback: Consumer<Difficulty>?): Builder {
            dialog.onDifficultyChosen = callback
            return this
        }

        fun setOnDialogClosed(callback: Runnable): Builder {
            dialog.addEventHandler(com.jfoenix.controls.events.JFXDialogEvent.CLOSED) { callback.run() }
            return this
        }

        fun build(): DifficultyDialog = dialog

        fun buildAndShow(): Unit = build().show()
    }
}
