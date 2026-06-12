package com.mj.tic.tac.toe.javafx.kotlin.controller.layout

import com.mj.tic.tac.toe.javafx.kotlin.controller.BaseController
import com.mj.tic.tac.toe.javafx.kotlin.controller.Subscriber
import com.mj.tic.tac.toe.javafx.kotlin.dialog.ConfirmDialog
import com.mj.tic.tac.toe.javafx.kotlin.engine.WinnerDetector
import com.mj.tic.tac.toe.javafx.kotlin.task.ComputerMoveTask
import com.mj.tic.tac.toe.javafx.kotlin.task.GameOverTask
import com.mj.tic.tac.toe.javafx.kotlin.task.MarkCellTask
import com.mj.tic.tac.toe.javafx.kotlin.util.Board
import com.mj.tic.tac.toe.javafx.kotlin.util.Difficulty
import com.mj.tic.tac.toe.javafx.kotlin.util.GameState
import com.mj.tic.tac.toe.javafx.kotlin.util.Mark
import com.mj.tic.tac.toe.javafx.kotlin.util.Player
import java.net.URL
import java.util.ResourceBundle
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javafx.application.Platform
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.concurrent.Task
import javafx.css.PseudoClass
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane

/**
 * Controller for the central NxN play area grid.
 *
 * This is the core game controller responsible for:
 * - Managing the [Board] model and its synchronization with the UI grid.
 * - Handling human player cell clicks and executing the corresponding
 *   [MarkCellTask] on a background thread.
 * - Automatically dispatching [ComputerMoveTask] when it becomes the
 *   AI's turn, with the move computed according to the current [Difficulty].
 * - Detecting win/draw conditions via [WinnerDetector] and triggering
 *   [GameOverTask] when the game reaches a terminal state.
 * - Showing a [ConfirmDialog] on game over, allowing the player to
 *   start a new game at the same difficulty.
 *
 * Implements [Subscriber] to react to [GameState] changes published
 * by the [GameStateManager].
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

class PlayAreaPanelController : BaseController(), Subscriber<Any> {

    /**
     * Single-thread daemon executor for background game tasks.
     *
     * All AI move computation and cell marking runs on this executor
     * to keep the UI responsive. Threads are daemon so they don't
     * prevent JVM exit.
     */
    private val executor: ExecutorService = Executors.newSingleThreadExecutor { threadFactory ->
        Thread(threadFactory).apply { isDaemon = true }
    }

    /**
     * Observable property for the current game difficulty.
     *
     * When null, the grid is disabled (no game in progress).
     * The grid's [javafx.scene.Node.disableProperty] is bidirectionally
     * bound to this property via [difficulty.isNull].
     */
    private val difficulty: ObjectProperty<Difficulty?> = SimpleObjectProperty()

    /**
     * Observable property tracking whose turn it is.
     *
     * On change: if the new value is [Player.COMPUTER], a
     * [ComputerMoveTask] is automatically dispatched. After each
     * human move, this property is flipped via [Player.opponent].
     */
    private val player: ObjectProperty<Player?> = SimpleObjectProperty()

    /** The game board model. Initialised as a standard 3x3 board. */
    private val board = Board()

    /**
     * Root [StackPane] of the application scene.
     *
     * Resolved during [initialize] and used as the container for
     * the [ConfirmDialog] overlay on game over.
     */
    private lateinit var applicationPane: StackPane

    /** The NxN grid of buttons representing the board cells. */
    @FXML
    private lateinit var rootPane: GridPane

    /**
     * Initializes the controller.
     *
     * Sets up:
     * - Application pane reference for dialog overlays.
     * - Grid disabled binding to [difficulty.isNull] (disabled when no game).
     * - Listeners for grid enable state, difficulty changes, and
     *   player changes.
     *
     * @param url Unused.
     * @param resources Unused.
     */
    override fun initialize(url: URL, resources: ResourceBundle) {
        Platform.runLater { applicationPane = rootPane.scene.root as StackPane }
        rootPane.disableProperty().addListener(OnPlayAreaDisableStateChanged())
        rootPane.disableProperty().bind(difficulty.isNull())
        difficulty.addListener(OnDifficultyValueChanged())
        player.addListener(OnPlayerValueChanged())
    }

    /**
     * Handles a human player clicking a cell button.
     *
     * Creates a [MarkCellTask] for the clicked button and executes
     * it on the background executor. The [onTaskSucceeded] callback
     * handles the result (win detection, turn switching, etc.).
     *
     * @param event The mouse click event whose source is the button.
     */
    @FXML
    private fun onCellClicked(event: MouseEvent) {
        val button = event.source as Button

        val task: Task<*> = MarkCellTask(board, button, player.get()!!)
        task.setOnSucceeded(::onTaskSucceeded)
        executor.execute(task)
    }

    /**
     * Subscriber callback for game state changes.
     *
     * Handles three states:
     * - **[GameState.RESET_GAME]:** Clears difficulty (disabling grid),
     *   triggers visual reset via [OnPlayAreaDisableStateChanged].
     * - **[GameState.NEW_GAME]:** Sets the difficulty (enabling grid),
     *   which triggers difficulty listener that resets the board and
     *   randomly selects the starting player.
     * - **[GameState.GAME_OVER]:** Shows a [ConfirmDialog] with the
     *   result message. On confirmation, restarts a new game at the
     *   same difficulty.
     *
     * @param state The published game state.
     * @param value The payload: [Difficulty] for NEW_GAME,
     *   [Player]? for GAME_OVER (null = draw), null for RESET_GAME.
     */
    override fun update(state: GameState, value: Any?) {
        when (state) {
            GameState.RESET_GAME -> {
                difficulty.set(null)
                OnPlayAreaDisableStateChanged().changed(null, null, false)
            }

            GameState.NEW_GAME -> {
                difficulty.set(null)
                value as Difficulty
                difficulty.set(value)
            }

            GameState.GAME_OVER -> {
                val messageKey = if (value != null) {
                    if ((value as Player) == Player.HUMAN) {
                        "message.alert.player.winner.human"
                    } else {
                        "message.alert.player.winner.computer"
                    }
                } else {
                    "message.alert.player.draw"
                }

                val lMessage = getLocalizedText(messageKey)
                val currentDifficulty = difficulty.get()
                difficulty.set(null)

                val onPlayAgain = Runnable { update(GameState.NEW_GAME, currentDifficulty) }
                ConfirmDialog.show(applicationPane, lMessage, onPlayAgain)
            }
        }
    }

    /**
     * Callback invoked when a background task completes successfully.
     *
     * Handles two task types:
     * - **[ComputerMoveTask]:** Extracts the chosen [Coordinates] and
     *   fires a synthetic mouse click event on the corresponding grid
     *   button to trigger [onCellClicked].
     * - **[MarkCellTask]:** Extracts the [Mark], runs [WinnerDetector]
     *   on the last-placed mark. If there is a winner or the board is
     *   full, dispatches [GameOverTask]. Otherwise, flips the turn to
     *   the opponent.
     *
     * @param event The worker state event whose source identifies the task.
     */
    private fun onTaskSucceeded(event: javafx.concurrent.WorkerStateEvent) {
        when (val worker = event.source as Task<*>) {
            is ComputerMoveTask -> {
                val coordinates = worker.value
                if (coordinates != null) {
                    val index = coordinates.x * board.dimension + coordinates.y
                    val mEvent = MouseEvent(
                        MouseEvent.MOUSE_CLICKED, 0.0, 0.0, 0.0, 0.0, MouseButton.PRIMARY,
                        1, false, false, false, false, false, false, false, false, false, false, null
                    )
                    rootPane.children?.get(index)?.fireEvent(mEvent)
                }
            }

            is MarkCellTask -> {
                val mark = worker.value as Mark
                val coordinates = mark.coordinates
                val oWinner = WinnerDetector.detect(board, coordinates)
                if (oWinner.isPresent || board.isFull()) {
                    mediator?.execute(GameOverTask(rootPane, oWinner.orElse(null)))
                } else {
                    player.get()?.opponent()?.let(player::set)
                }
            }
        }
    }

    /**
     * Listener for the grid's enabled/disabled state changes.
     *
     * When the grid becomes **enabled** (a new game starts), resets
     * all button visuals: clears text, re-enables buttons, and
     * removes the `"winner"` pseudo-class from any previously
     * highlighted cells.
     */
    private inner class OnPlayAreaDisableStateChanged : ChangeListener<Boolean> {
        override fun changed(observable: ObservableValue<out Boolean>?, oldValue: Boolean?, newValue: Boolean?) {
            if (newValue!!.not()) {
                for (button in rootPane.children) {
                    button as Button
                    button.text = null
                    button.isDisable = false
                    button.pseudoClassStateChanged(PseudoClass.getPseudoClass("winner"), false)
                }
            }
        }
    }

    /**
     * Listener for the difficulty property changes.
     *
     * When a non-null difficulty is set (new game starts):
     * - Randomly selects the starting player (HUMAN or COMPUTER)
     *   by rounding a random double between 0 and 2.
     * - Resets the board model.
     * The starting player assignment triggers [OnPlayerValueChanged],
     * which dispatches a [ComputerMoveTask] if it's the computer's turn.
     *
     * When difficulty is cleared (null): resets the player property.
     */
    private inner class OnDifficultyValueChanged : ChangeListener<Difficulty?> {
        override fun changed(
            observable: ObservableValue<out Difficulty?>?,
            oldValue: Difficulty?,
            newValue: Difficulty?
        ) {
            if (newValue != null) {
                Player.from(((kotlin.math.round(kotlin.random.Random.nextDouble()) + 1).toInt()).toByte())
                    .let { player.set(it) }
            } else {
                player.set(null)
            }
            board.reset()
        }
    }

    /**
     * Listener for the player turn property changes.
     *
     * When the turn becomes [Player.COMPUTER], creates a
     * [ComputerMoveTask] with the current board state, difficulty,
     * and computer player, and executes it on the background executor.
     * The [onTaskSucceeded] callback is attached to fire a synthetic
     * click on the chosen cell.
     */
    private inner class OnPlayerValueChanged : ChangeListener<Player?> {
        override fun changed(observable: ObservableValue<out Player?>?, oldValue: Player?, newValue: Player?) {
            if (newValue == Player.COMPUTER) {
                val task = ComputerMoveTask(board, difficulty.get()!!, newValue)
                task.setOnSucceeded(this@PlayAreaPanelController::onTaskSucceeded)
                executor.execute(task)
            }
        }
    }
}
