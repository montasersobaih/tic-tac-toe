package com.mj.tic.tac.toe.javafx.kotlin.task

import com.mj.tic.tac.toe.javafx.kotlin.util.Coordinates
import com.mj.tic.tac.toe.javafx.kotlin.util.Mark
import com.mj.tic.tac.toe.javafx.kotlin.util.Winner

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

class CheckWinnerTask : BaseTask<Winner?> {

    private val matrix: Array<ByteArray>

    private val mark: Mark

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(matrix: Array<ByteArray>, mark: Mark) : super() {
        this.matrix = matrix
        this.mark = mark
    }

    override fun call(): Winner? {
        val locations = this.check()
        return if (locations.size == 3) Winner(mark.value, locations) else null
    }

    private fun check(): List<Coordinates> {
        val (x, y) = mark.coordinates
        val value = matrix[x][y]
        val coordinates: MutableList<Coordinates> = ArrayList()

        //check row
        for (i in matrix[x].indices) {
            if (matrix[x][i] == value) {
                coordinates.add(Coordinates(x, i))
                if (coordinates.size == 3) {
                    return coordinates
                }
            }
        }

        //check column
        coordinates.clear()
        for (i in matrix.indices) {
            if (matrix[i][y] == value) {
                coordinates.add(Coordinates(i, y))
                if (coordinates.size == 3) {
                    return coordinates
                }
            }
        }

        //check diagonal
        coordinates.clear()
        for (i in matrix.indices) {
            if (matrix[i][i] == value) {
                coordinates.add(Coordinates(i, i))
                if (coordinates.size == 3) {
                    return coordinates
                }
            }
        }

        //check reverse diagonal
        coordinates.clear()
        for (i in matrix.indices.reversed()) {
            val lastIndex = matrix.size - 1
            if (matrix[lastIndex - i][i] == value) {
                coordinates.add(Coordinates(lastIndex - i, i))
                if (coordinates.size == 3) {
                    return coordinates
                }
            }
        }

        return emptyList()
    }
}