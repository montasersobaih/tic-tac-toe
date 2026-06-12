package com.mj.tic.tac.toe.javafx.kotlin.task

import com.mj.tic.tac.toe.javafx.kotlin.util.Board
import com.mj.tic.tac.toe.javafx.kotlin.util.Coordinates
import com.mj.tic.tac.toe.javafx.kotlin.util.Mark
import com.mj.tic.tac.toe.javafx.kotlin.util.Player
import javafx.scene.control.Button
import javafx.scene.layout.GridPane

/**
 * Background task that records a player's move on the board model
 * and updates the corresponding UI button.
 *
 * When executed:
 * 1. [call] runs on the background thread: reads the button's grid
 *    coordinates, marks the [Board] model, and returns a [Mark] with
 *    the alternating symbol ('X' or 'O').
 * 2. [succeeded] runs on the JavaFX Application Thread: disables the
 *    button (preventing re-clicks) and sets its text to the symbol.
 *
 * The symbol is determined by [Board.markedCellsCount] modulo 2, so
 * the first move gets 'X', the second 'O', the third 'X', and so on.
 *
 * @property board The board model to mark.
 * @property button The UI button representing the clicked cell.
 * @property player The player making the move.
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 22-01-2023
 */

class MarkCellTask : BaseTask<Mark> {

    /** Alternating symbols for the two players. */
    private val symbols = charArrayOf('X', 'O')

    private val board: Board

    private val button: Button

    private val player: Player

    /**
     * Creates a new [MarkCellTask] for the given board, button, and player.
     *
     * @param board The board model to update.
     * @param button The UI button that was clicked.
     * @param player The player making the move.
     */
    constructor(board: Board, button: Button, player: Player) : super() {
        this.board = board
        this.button = button
        this.player = player
    }

    /**
     * Records the move on the board and returns the resulting [Mark].
     *
     * Reads the button's grid row/column indices via [GridPane.getRowIndex]
     * and [GridPane.getColumnIndex], marks the board at those coordinates,
     * and creates a [Mark] with the alternating symbol.
     *
     * @return A [Mark] containing the symbol and coordinates of the move.
     */
    override fun call(): Mark {
        val x = GridPane.getRowIndex(button)
        val y = GridPane.getColumnIndex(button)
        val coordinates = Coordinates(x, y)

        board.mark(coordinates, player)
        return Mark(symbols[board.markedCellsCount % 2], coordinates)
    }

    /**
     * Updates the button UI on the FX thread after the task succeeds.
     *
     * Disables the button to prevent it from being clicked again and
     * sets its text to the player's symbol ('X' or 'O').
     */
    override fun succeeded() {
        button.isDisable = true
        button.text = value.symbol.toString()
    }
}