package com.mj.tic.tac.toe.javafx.kotlin.controller.layout

import com.mj.tic.tac.toe.javafx.kotlin.controller.BaseController
import com.mj.tic.tac.toe.javafx.kotlin.controller.Subscriber
import com.mj.tic.tac.toe.javafx.kotlin.task.PlayGameTask
import com.mj.tic.tac.toe.javafx.kotlin.task.ResetGameTask
import com.mj.tic.tac.toe.javafx.kotlin.util.Difficulty
import com.mj.tic.tac.toe.javafx.kotlin.util.GameState
import com.mj.tic.tac.toe.javafx.kotlin.util.Player
import java.net.URL
import java.util.Optional
import java.util.ResourceBundle
import javafx.application.Platform
import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

class LeftPanelController : BaseController(), Subscriber<Any> {

    private lateinit var applicationPane: StackPane

    @FXML
    private lateinit var rootPane: BorderPane

    @FXML
    private lateinit var displayDifficulty: Label

    @FXML
    private lateinit var wins: ListView<String>

    @FXML
    private lateinit var totalDraw: Label

    @FXML
    private lateinit var totalHumanWins: Label

    @FXML
    private lateinit var totalMachineWins: Label

    override fun initialize(url: URL, resources: ResourceBundle) {
        Platform.runLater { applicationPane = rootPane.scene.root as StackPane }
        wins.items.addListener(OnListChangeListener())
    }

    @FXML
    private fun onResetGame(event: MouseEvent?) {
        listOf(totalDraw, totalHumanWins, totalMachineWins).forEach { it.text = "0" }
        wins.items.clear()

        if (event != null) {
            Optional.ofNullable("control.label.difficulty.default")
                .map(::getLocalizedText)
                .ifPresent(displayDifficulty::setText)
            mediator?.execute(ResetGameTask())
        }
    }

    @FXML
    private fun onPlayGame(event: MouseEvent?) {
//        DifficultyDialog.showAndWait(applicationPane)
        mediator?.execute(PlayGameTask(applicationPane))
    }

    override fun update(state: GameState, value: Any?) {
        when (state) {
            GameState.RESET_GAME -> {}
            GameState.NEW_GAME -> {
                Optional.ofNullable(value)
                    .map { it as Difficulty }
                    .map(Difficulty::toString)
                    .map { super.getLocalizedText(it) }
                    .ifPresent { localizedText ->
                        this.onResetGame(null)
                        displayDifficulty.text = localizedText
                    }
            }

            GameState.GAME_OVER -> {
                val wPlayer = value as? Player

                var messageKey = "message.alert.player.draw"
                var totalWinsLabel = totalDraw

                if (wPlayer != null) {
                    messageKey = if (wPlayer == Player.HUMAN) {
                        "message.alert.player.winner.human"
                    } else {
                        "message.alert.player.winner.computer"
                    }

                    totalWinsLabel = if (wPlayer == Player.HUMAN) totalHumanWins else totalMachineWins
                }

                Optional.ofNullable(messageKey)
                    .map { super.getLocalizedText(it) }
                    .map { it.split(", ")[0] }
                    .ifPresent(wins.items::add)
                Optional.ofNullable(totalWinsLabel)
                    .map { it.text }
                    .map { it.toInt() }
                    .map { it.inc() }
                    .map { it.toString() }
                    .ifPresent(totalWinsLabel::setText)
            }
        }
    }

    private inner class OnListChangeListener : ListChangeListener<String> {
        override fun onChanged(c: ListChangeListener.Change<out String>) {
            if (c.next() && c.wasAdded()) {
                Platform.runLater { wins.scrollTo(c.list.size - 1) }
            }
        }
    }
}
