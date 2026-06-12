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
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

class PlayAreaPanelController : BaseController(), Subscriber<Any> {

    private val executor: ExecutorService = Executors.newSingleThreadExecutor { threadFactory ->
        Thread(threadFactory).apply { isDaemon = true }
    }

    private val difficulty: ObjectProperty<Difficulty?> = SimpleObjectProperty()

    private val player: ObjectProperty<Player?> = SimpleObjectProperty()

    private val board = Board()

    private lateinit var applicationPane: StackPane

    @FXML
    private lateinit var rootPane: GridPane

    override fun initialize(url: URL, resources: ResourceBundle) {
        Platform.runLater { applicationPane = rootPane.scene.root as StackPane }
        rootPane.disableProperty().addListener(OnPlayAreaDisableStateChanged())
        rootPane.disableProperty().bind(difficulty.isNull())
        difficulty.addListener(OnDifficultyValueChanged())
        player.addListener(OnPlayerValueChanged())
    }

    @FXML
    private fun onCellClicked(event: MouseEvent) {
        val button = event.source as Button

        val task: Task<*> = MarkCellTask(board, button, player.get()!!)
        task.setOnSucceeded(::onTaskSucceeded)
        executor.execute(task)
    }

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
