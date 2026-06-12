package com.mj.tic.tac.toe.javafx.kotlin.controller.view

import com.mj.tic.tac.toe.javafx.kotlin.controller.BaseController
import com.mj.tic.tac.toe.javafx.kotlin.controller.ControllerMediator
import com.mj.tic.tac.toe.javafx.kotlin.controller.GameStateManager
import com.mj.tic.tac.toe.javafx.kotlin.controller.layout.ApplicationBarController
import com.mj.tic.tac.toe.javafx.kotlin.controller.layout.LeftPanelController
import com.mj.tic.tac.toe.javafx.kotlin.controller.layout.PlayAreaPanelController
import com.mj.tic.tac.toe.javafx.kotlin.task.GameOverTask
import com.mj.tic.tac.toe.javafx.kotlin.task.PlayGameTask
import com.mj.tic.tac.toe.javafx.kotlin.task.ResetGameTask
import com.mj.tic.tac.toe.javafx.kotlin.util.GameState
import java.net.URL
import java.util.ResourceBundle
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.concurrent.Worker
import javafx.concurrent.WorkerStateEvent
import javafx.fxml.FXML
import javafx.scene.layout.StackPane

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

class ViewController : BaseController(), ControllerMediator {

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    private val gameStateManager = GameStateManager()

    @FXML
    private lateinit var viewPane: StackPane

    @FXML
    private lateinit var applicationBarController: ApplicationBarController

    @FXML
    private lateinit var leftPanelController: LeftPanelController

    @FXML
    private lateinit var playAreaPanelController: PlayAreaPanelController

    override fun initialize(url: URL, resources: ResourceBundle) {
        Platform.runLater { viewPane.scene?.window?.setOnCloseRequest { executor.shutdown() } }

        applicationBarController.setTitle(getLocalizedText("control.label.title.app"))

        listOf(leftPanelController, playAreaPanelController).forEach { it.setMediator(this) }

        gameStateManager.subscribe(GameState.RESET_GAME, playAreaPanelController)
        gameStateManager.subscribe(GameState.NEW_GAME, leftPanelController, playAreaPanelController)
        gameStateManager.subscribe(GameState.GAME_OVER, leftPanelController, playAreaPanelController)
    }

    override fun execute(task: Task<*>) {
        task.setOnSucceeded(this::onTaskSucceeded)
        executor.execute(task)
    }

    fun onTaskSucceeded(event: WorkerStateEvent) {
        when (val worker = event.source as Worker<*>) {
            is ResetGameTask -> {
                executor.execute { gameStateManager.publish<Any>(GameState.RESET_GAME) }
            }

            is PlayGameTask -> {
                val difficulty = worker.value
                if (difficulty != null) {
                    executor.execute { gameStateManager.publish(GameState.NEW_GAME, difficulty) }
                }
            }

            is GameOverTask -> {
                val wPlayer = worker.value
                executor.execute { gameStateManager.publish(GameState.GAME_OVER, wPlayer) }
            }
        }
    }
}
