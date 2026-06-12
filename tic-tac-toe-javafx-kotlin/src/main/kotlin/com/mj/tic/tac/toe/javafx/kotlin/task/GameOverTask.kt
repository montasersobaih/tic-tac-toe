package com.mj.tic.tac.toe.javafx.kotlin.task

import com.mj.tic.tac.toe.javafx.kotlin.util.Player
import com.mj.tic.tac.toe.javafx.kotlin.util.Winner
import javafx.application.Platform
import javafx.css.PseudoClass
import javafx.scene.control.Button
import javafx.scene.layout.GridPane

/**
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
