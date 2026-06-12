package com.mj.tic.tac.toe.javafx.kotlin.task

import com.mj.tic.tac.toe.javafx.kotlin.util.Board
import com.mj.tic.tac.toe.javafx.kotlin.util.Coordinates
import com.mj.tic.tac.toe.javafx.kotlin.util.Mark
import com.mj.tic.tac.toe.javafx.kotlin.util.Player
import javafx.scene.control.Button
import javafx.scene.layout.GridPane

/**
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 22-01-2023
 */

class MarkCellTask : BaseTask<Mark> {

    private val symbols = charArrayOf('X', 'O')

    private val board: Board

    private val button: Button

    private val player: Player

    constructor(board: Board, button: Button, player: Player) : super() {
        this.board = board
        this.button = button
        this.player = player
    }

    override fun call(): Mark {
        val x = GridPane.getRowIndex(button)
        val y = GridPane.getColumnIndex(button)
        val coordinates = Coordinates(x, y)

        board.mark(coordinates, player)
        return Mark(symbols[board.markedCellsCount % 2], coordinates)
    }

    override fun succeeded() {
        button.isDisable = true
        button.text = value.symbol.toString()
    }
}