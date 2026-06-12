package com.mj.tic.tac.toe.javafx.kotlin.engine

import com.mj.tic.tac.toe.javafx.kotlin.util.Board
import com.mj.tic.tac.toe.javafx.kotlin.util.Coordinates
import com.mj.tic.tac.toe.javafx.kotlin.util.Player
import com.mj.tic.tac.toe.javafx.kotlin.util.Winner
import java.util.Optional
import java.util.concurrent.CompletableFuture

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

object WinnerDetector {

    fun detect(board: Board): Optional<Winner> {
        val rowFut = CompletableFuture.supplyAsync { winningRowDetection(board) }
        val colFut = CompletableFuture.supplyAsync { winningColumnDetection(board) }
        val diagFut = CompletableFuture.supplyAsync { winningDiagonalDetection(board) }

        return CompletableFuture
            .allOf(rowFut, colFut, diagFut)
            .thenApply {
                sequenceOf(rowFut, colFut, diagFut)
                    .map { it.join() }
                    .filterNotNull()
                    .firstOrNull()
                    .let { Optional.ofNullable(it) }
            }
            .exceptionally { Optional.empty() }
            .join()
    }

    fun detect(board: Board, coordinates: Coordinates): Optional<Winner> {
        val rowFut = CompletableFuture.supplyAsync { winningRowDetection(board, coordinates) }
        val colFut = CompletableFuture.supplyAsync { winningColumnDetection(board, coordinates) }
        val diagFut = CompletableFuture.supplyAsync { winningDiagonalDetection(board) }

        return CompletableFuture
            .allOf(rowFut, colFut, diagFut)
            .thenApply {
                sequenceOf(rowFut, colFut, diagFut)
                    .map { it.join() }
                    .filterNotNull()
                    .firstOrNull()
                    .let { Optional.ofNullable(it) }
            }
            .exceptionally { Optional.empty() }
            .join()
    }

    private fun winningRowDetection(board: Board): Winner? {
        for (x in 0 until board.dimension) {
            val winningLine = mutableListOf(Coordinates(x, 0))
            val pValue = board.get(winningLine[0])
            for (y in 1 until board.dimension) {
                if (pValue != 0.toByte() && pValue == board.get(Coordinates(x, y))) {
                    winningLine.add(Coordinates(x, y))
                } else {
                    break
                }
                if (winningLine.size == board.dimension) {
                    return Winner(Player.from(pValue), winningLine)
                }
            }
        }
        return null
    }

    private fun winningRowDetection(board: Board, coordinates: Coordinates): Winner? {
        val winningLine = mutableListOf(Coordinates(coordinates.x, 0))
        val pValue = board.get(winningLine[0])
        for (y in 1 until board.dimension) {
            val yCoord = coordinates.copy(y = y)
            if (board.get(yCoord) == pValue) {
                winningLine.add(yCoord)
            } else {
                return null
            }
            if (winningLine.size == board.dimension) {
                return Winner(Player.from(pValue), winningLine)
            }
        }
        return null
    }

    private fun winningColumnDetection(board: Board): Winner? {
        for (y in 0 until board.dimension) {
            val winningLine = mutableListOf(Coordinates(0, y))
            val pValue = board.get(winningLine[0])
            for (x in 1 until board.dimension) {
                if (pValue != 0.toByte() && pValue == board.get(Coordinates(x, y))) {
                    winningLine.add(Coordinates(x, y))
                } else {
                    break
                }
                if (winningLine.size == board.dimension) {
                    return Winner(Player.from(pValue), winningLine)
                }
            }
        }
        return null
    }

    private fun winningColumnDetection(board: Board, coordinates: Coordinates): Winner? {
        val winningLine = mutableListOf(Coordinates(0, coordinates.y))
        val pValue = board.get(winningLine[0])
        for (x in 1 until board.dimension) {
            val xCoord = coordinates.copy(x = x)
            if (pValue == board.get(xCoord)) {
                winningLine.add(xCoord)
            } else {
                return null
            }
            if (winningLine.size == board.dimension) {
                return Winner(Player.from(pValue), winningLine)
            }
        }
        return null
    }

    private fun winningDiagonalDetection(board: Board): Winner? {
        var winningLine = mutableListOf<Coordinates>()
        var pValue = board.get(Coordinates(0, 0))
        if (pValue != 0.toByte()) {
            for (i in 0 until board.dimension) {
                if (pValue == board.get(Coordinates(i, i))) {
                    winningLine.add(Coordinates(i, i))
                } else {
                    break
                }
            }
            if (winningLine.size == board.dimension) {
                return Winner(Player.from(pValue), winningLine)
            }
        }

        winningLine = mutableListOf()
        pValue = board.get(Coordinates(0, board.dimension - 1))
        if (pValue != 0.toByte()) {
            for (i in 0 until board.dimension) {
                val y = board.dimension - 1 - i
                if (pValue == board.get(Coordinates(i, y))) {
                    winningLine.add(Coordinates(i, y))
                } else {
                    break
                }
            }
            if (winningLine.size == board.dimension) {
                return Winner(Player.from(pValue), winningLine)
            }
        }

        return null
    }
}
