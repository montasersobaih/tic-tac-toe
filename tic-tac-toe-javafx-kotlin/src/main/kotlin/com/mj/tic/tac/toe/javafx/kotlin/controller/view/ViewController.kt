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
 * Top-level controller for the main application view.
 *
 * This controller is the root of the FXML-defined view hierarchy and
 * serves as the central coordination point for the entire application.
 * It has three key responsibilities:
 *
 * **1. Mediator:** Implements [ControllerMediator] to provide a single
 * point through which all child controllers execute background tasks.
 * Results are routed through [onTaskSucceeded] to the [GameStateManager].
 *
 * **2. Event Bus Owner:** Owns the [GameStateManager] instance and
 * subscribes child controllers to relevant [GameState] events during
 * [initialize]. Task results are translated into published events.
 *
 * **3. Controller Graph Wiring:** Injects the mediator reference into
 * child controllers ([LeftPanelController], [PlayAreaPanelController])
 * and sets up the initial application state (window title, close handler).
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

class ViewController : BaseController(), ControllerMediator {

    /**
     * Single-thread executor for all background game tasks.
     *
     * Shut down when the window close request is received, ensuring
     * no tasks are left running after the application exits.
     */
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    /**
     * Publish-subscribe event bus for game state changes.
     *
     * Child controllers subscribe to [GameState] values during
     * [initialize] and are notified when tasks complete and
     * their results are published.
     */
    private val gameStateManager = GameStateManager()

    /** Root container of the entire view, injected from FXML. */
    @FXML
    private lateinit var viewPane: StackPane

    /** FXML-injected child controller for the custom title bar. */
    @FXML
    private lateinit var applicationBarController: ApplicationBarController

    /** FXML-injected child controller for the left score panel. */
    @FXML
    private lateinit var leftPanelController: LeftPanelController

    /** FXML-injected child controller for the play area grid. */
    @FXML
    private lateinit var playAreaPanelController: PlayAreaPanelController

    /**
     * Initializes the controller graph and event bus subscriptions.
     *
     * Performs the following setup:
     * 1. Registers a window close handler to shut down the executor.
     * 2. Sets the localized application title on the title bar.
     * 3. Injects this controller (as [ControllerMediator]) into
     *    [leftPanelController] and [playAreaPanelController].
     * 4. Subscribes both controllers to [GameState.RESET_GAME],
     *    [GameState.NEW_GAME], and [GameState.GAME_OVER] events.
     *
     * @param url Unused.
     * @param resources Unused.
     */
    override fun initialize(url: URL, resources: ResourceBundle) {
        Platform.runLater { viewPane.scene?.window?.setOnCloseRequest { executor.shutdown() } }

        applicationBarController.setTitle(getLocalizedText("control.label.title.app"))

        listOf(leftPanelController, playAreaPanelController).forEach { it.setMediator(this) }

        gameStateManager.subscribe(GameState.RESET_GAME, playAreaPanelController)
        gameStateManager.subscribe(GameState.NEW_GAME, leftPanelController, playAreaPanelController)
        gameStateManager.subscribe(GameState.GAME_OVER, leftPanelController, playAreaPanelController)
    }

    /**
     * Executes a background task through the mediator.
     *
     * Attaches [onTaskSucceeded] as the success event handler,
     * then submits the task to the single-thread executor.
     *
     * @param task The task to execute. Expected types are:
     *   [ResetGameTask], [PlayGameTask], [GameOverTask].
     */
    override fun execute(task: Task<*>) {
        task.setOnSucceeded(this::onTaskSucceeded)
        executor.execute(task)
    }

    /**
     * Routes completed task results to the [GameStateManager] for
     * publication to subscribers.
     *
     * Handles three task types:
     * - **[ResetGameTask]:** Publishes [GameState.RESET_GAME] with
     *   no payload.
     * - **[PlayGameTask]:** Publishes [GameState.NEW_GAME] with
     *   the chosen [Difficulty] as payload (only if non-null).
     * - **[GameOverTask]:** Publishes [GameState.GAME_OVER] with
     *   the winning [Player] (or null for draw) as payload.
     *
     * All publications happen on the executor thread; [GameStateManager]
     * ensures updates are dispatched to the FX thread via
     * [Platform.runLater] when necessary.
     *
     * @param event The worker state event whose source identifies
     *   the completed task.
     */
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
