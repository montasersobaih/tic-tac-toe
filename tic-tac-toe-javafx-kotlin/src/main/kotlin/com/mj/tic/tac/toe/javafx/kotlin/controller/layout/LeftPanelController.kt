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
 * Controller for the left-side panel displaying game statistics.
 *
 * This panel shows:
 * - The current difficulty level label.
 * - A scrollable win/loss/draw history list.
 * - Score counters for draws, human wins, and computer wins.
 * - "Play" and "Reset" buttons.
 *
 * Implements [Subscriber] to react to [GameState.NEW_GAME] and
 * [GameState.GAME_OVER] events published by the [GameStateManager].
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

class LeftPanelController : BaseController(), Subscriber<Any> {

    /**
     * The root [StackPane] of the entire application scene.
     *
     * Resolved during [initialize] for use as the dialog container
     * when showing the [DifficultyDialog].
     */
    private lateinit var applicationPane: StackPane

    /** Root layout of the left panel, injected from FXML. */
    @FXML
    private lateinit var rootPane: BorderPane

    /** Label displaying the current difficulty level. */
    @FXML
    private lateinit var displayDifficulty: Label

    /** ListView for the scrollable game result history. */
    @FXML
    private lateinit var wins: ListView<String>

    /** Label showing the total number of draws. */
    @FXML
    private lateinit var totalDraw: Label

    /** Label showing the total number of human wins. */
    @FXML
    private lateinit var totalHumanWins: Label

    /** Label showing the total number of computer wins. */
    @FXML
    private lateinit var totalMachineWins: Label

    /**
     * Initializes the controller.
     *
     * Resolves the application [StackPane] from the scene root for
     * dialog overlays. Attaches [OnListChangeListener] to the wins
     * list items to enable auto-scrolling when new results are added.
     *
     * @param url Unused.
     * @param resources Unused.
     */
    override fun initialize(url: URL, resources: ResourceBundle) {
        Platform.runLater { applicationPane = rootPane.scene.root as StackPane }
        wins.items.addListener(OnListChangeListener())
    }

    /**
     * Handles the "Reset Game" action.
     *
     * Resets all score counters to "0" and clears the win history list.
     * If called from a button click (non-null event), also resets the
     * difficulty label to the default text and dispatches a
     * [ResetGameTask] through the mediator to notify all subscribers.
     *
     * @param event The mouse event, or null when called programmatically.
     */
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

    /**
     * Handles the "Play Game" action.
     *
     * Dispatches a [PlayGameTask] through the mediator, which shows
     * the [DifficultyDialog] and returns the chosen difficulty level.
     *
     * @param event Unused mouse event.
     */
    @FXML
    private fun onPlayGame(event: MouseEvent?) {
        mediator?.execute(PlayGameTask(applicationPane))
    }

    /**
     * Subscriber callback for game state changes.
     *
     * Handles three states:
     * - **[GameState.RESET_GAME]:** No-op (reset is handled in [onResetGame]).
     * - **[GameState.NEW_GAME]:** Updates the difficulty label with the
     *   localized difficulty name and resets the game board.
     * - **[GameState.GAME_OVER]:** Determines the result (human win,
     *   computer win, or draw), increments the appropriate counter,
     *   and adds the localized result message to the win history list.
     *
     * @param state The published game state.
     * @param value The payload: [Difficulty] for NEW_GAME,
     *   [Player]? (or null for draw) for GAME_OVER, null for RESET_GAME.
     */
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

    /**
     * Inner [ListChangeListener] that auto-scrolls the win history list.
     *
     * When new items are added to the [wins] list, the listener
     * scrolls to the bottom so the latest result is always visible
     * to the user.
     */
    private inner class OnListChangeListener : ListChangeListener<String> {
        override fun onChanged(c: ListChangeListener.Change<out String>) {
            if (c.next() && c.wasAdded()) {
                Platform.runLater { wins.scrollTo(c.list.size - 1) }
            }
        }
    }
}
