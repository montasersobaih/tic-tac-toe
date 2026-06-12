package com.mj.tic.tac.toe.javafx.kotlin.task

import com.mj.tic.tac.toe.javafx.kotlin.util.Player
import com.mj.tic.tac.toe.javafx.kotlin.util.Winner
import javafx.application.Platform
import javafx.css.PseudoClass
import javafx.scene.control.Button
import javafx.scene.layout.GridPane

/**
 * Background task that handles post-game visual updates and returns
 * the game result.
 *
 * When a winner exists, this task highlights the winning cells by
 * applying the `"winner"` CSS pseudo-class to the corresponding
 * buttons in the grid pane. The pseudo-class triggers CSS styling
 * to visually distinguish the winning line (e.g., changing background
 * color or adding an animation).
 *
 * The task returns the winning [Player] (or null for a draw) so that
 * [ViewController.onTaskSucceeded] can publish it as the
 * [GameState.GAME_OVER] payload.
 *
 * @property boardPane The grid of buttons representing the board.
 * @property winner The winner information, or null for a draw.
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

class GameOverTask(
    private val boardPane: GridPane,
    private val winner: Winner?
) : BaseTask<Player?>() {

    /**
     * Highlights winning cells and returns the result player.
     *
     * If [winner] is non-null, iterates the winning [Coordinates],
     * resolves each to its button index via `x * dimension + y`,
     * and applies the `"winner"` [PseudoClass] on the FX thread.
     *
     * @return The winning [Player], or null if the game was a draw.
     */
    override fun call(): Player? {
        var player: Player? = null

        if (winner != null) {
            player = winner.player

            val boardDimension = boardPane.rowCount
            val buttons = boardPane.children
            for (location in winner.locations) {
                val index = location.x * boardDimension + location.y
                val button = buttons[index] as Button
                Platform.runLater {
                    button.pseudoClassStateChanged(PseudoClass.getPseudoClass("winner"), true)
                }
            }
        }

        return player
    }
}
