package com.mj.tic.tac.toe.javafx.kotlin.task

import com.mj.tic.tac.toe.javafx.kotlin.util.Coordinates
import com.mj.tic.tac.toe.javafx.kotlin.util.Mark
import javafx.scene.control.Button
import javafx.scene.layout.GridPane

/**
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 22-01-2023
 */

class MarkCellTask : BaseTask<Mark?> {

    private val player = charArrayOf('X', 'O')

    private val matrix: Array<ByteArray>

    private val button: Button

    private val turn: Int

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(matrix: Array<ByteArray>, button: Button, turn: Int) : super() {
        this.matrix = matrix
        this.button = button
        this.turn = turn
    }

    override fun call(): Mark {
        val i = GridPane.getRowIndex(button)
        val j = GridPane.getColumnIndex(button)
        val coordinates = Coordinates(i, j)

        matrix[i][j] = (turn + 1).toByte()
        return Mark(player[turn], coordinates)
    }

    override fun succeeded() {
        button.isDisable = true
        button.text = Character.toString(player[turn])
    }
}