package com.mj.tic.tac.toe.javafx.kotlin.controller.view

import com.mj.tic.tac.toe.javafx.kotlin.controller.BaseController
import com.mj.tic.tac.toe.javafx.kotlin.controller.layout.ApplicationBarController
import com.mj.tic.tac.toe.javafx.kotlin.dialog.ConfirmDialog
import com.mj.tic.tac.toe.javafx.kotlin.task.CheckWinnerTask
import com.mj.tic.tac.toe.javafx.kotlin.task.FinishGameTask
import com.mj.tic.tac.toe.javafx.kotlin.task.MarkCellTask
import com.mj.tic.tac.toe.javafx.kotlin.task.ResetPlayAreaTask
import javafx.concurrent.WorkerStateEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import javafx.stage.Window
import java.net.URL
import java.util.Objects
import java.util.ResourceBundle

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

class ViewController : BaseController() {

    @FXML
    private lateinit var viewPane: StackPane

    @FXML
    private lateinit var applicationBarController: ApplicationBarController

    @FXML
    private lateinit var contentPane: StackPane

    @FXML
    private lateinit var wins: ListView<String>

    @FXML
    private lateinit var totalDraw: Label

    @FXML
    private lateinit var totalXWins: Label

    @FXML
    private lateinit var totalOWins: Label

    @FXML
    private lateinit var playAreaPane: GridPane

    override fun initialize(url: URL, resources: ResourceBundle) {
        viewPane.sceneProperty().addListener { _, _, scene: Scene ->
            scene.windowProperty().addListener { _, _, window: Window ->
                window.setOnCloseRequest { executor.shutdown() }
            }
        }

        applicationBarController.setTitle(getString("control.label.title.app"))
    }

    private fun resetPlayArea() {
        val task = ResetPlayAreaTask(matrix, playAreaPane)
        task.onSucceeded = EventHandler { onTaskSucceeded(it) }
        executor.execute(task)
    }

    @FXML
    private fun onResetGame(event: MouseEvent) {
        wins.items.clear()
        totalDraw.text = "0"
        totalXWins.text = "0"
        totalOWins.text = "0"
        playAreaPane.isDisable = true
        this.resetPlayArea()
    }

    @FXML
    private fun onPlayGame(event: MouseEvent) {
        playAreaPane.isDisable = false
        this.resetPlayArea()
    }

    @FXML
    private fun onCellClicked(event: MouseEvent) {
        val button = event.source as Button
        val task = MarkCellTask(matrix, button, super.turn.toInt())
        task.setOnSucceeded(::onTaskSucceeded)
        executor.execute(task)
    }

    private fun onTaskSucceeded(event: WorkerStateEvent) {
        val worker = event.source
        if (worker is ResetPlayAreaTask) {
            super.count = 0
        } else if (worker is MarkCellTask) {
            val task = CheckWinnerTask(matrix, worker.value!!)
            task.onSucceeded = EventHandler { onTaskSucceeded(it) }
            executor.execute(task)
        } else if (worker is CheckWinnerTask) {
            val winner = worker.value
            if (Objects.nonNull(winner) || ++super.count == (9).toByte()) {
                val label = when (winner) {
                    null -> totalDraw
                    else -> if (super.turn.toInt() == 0) totalXWins else totalOWins
                }

                val task = FinishGameTask(winner!!, playAreaPane, wins, label)
                task.setOnSucceeded(::onTaskSucceeded)
                executor.execute(task)
            }
        } else if (worker is FinishGameTask) {
            ConfirmDialog.getInstance(contentPane)
                .setMessage(worker.getMessage())
                .setOnConfirmListener { resetPlayArea() }
                .setOnDeclineListener { playAreaPane.isDisable = true }
                .build()
                .show()
        }
    }
}