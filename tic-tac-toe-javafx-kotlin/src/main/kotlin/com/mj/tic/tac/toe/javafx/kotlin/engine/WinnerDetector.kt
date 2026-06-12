package com.mj.tic.tac.toe.javafx.kotlin.engine

import com.mj.tic.tac.toe.javafx.kotlin.engine.WinnerDetector.detect
import com.mj.tic.tac.toe.javafx.kotlin.util.Board
import com.mj.tic.tac.toe.javafx.kotlin.util.Coordinates
import com.mj.tic.tac.toe.javafx.kotlin.util.Player
import com.mj.tic.tac.toe.javafx.kotlin.util.Winner
import java.util.Optional
import java.util.concurrent.CompletableFuture

/**
 * Singleton that checks for a winning board configuration using
 * parallel asynchronous detection.
 *
 * All four line types (rows, columns, main diagonal, anti-diagonal)
 * are checked concurrently via [CompletableFuture.supplyAsync]. This
 * leverages multi-core processors to reduce detection latency,
 * especially on larger board dimensions.
 *
 * Two overloads of [detect] are provided:
 * - A full-board scan that checks every row, column, and diagonal.
 * - An optimized variant that only checks lines intersecting a given
 *   [Coordinates] (the last-placed mark), since only those lines
 *   could have become winning due to the last move.
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

object WinnerDetector {

    /**
     * Scans the entire board for a winner across all rows, columns,
     * and diagonals, running detection in parallel.
     *
     * @param board The board to check for a winning configuration.
     * @return An [Optional] containing the [Winner] if found, or
     *   [Optional.empty] if there is no winner.
     */
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

    /**
     * Optimised detection that only checks lines intersecting the
     * given [coordinates] (the last-placed mark).
     *
     * Since only the row, column, and diagonals that pass through
     * the last move could possibly have been completed by that move,
     * this variant is more efficient than a full-board scan.
     *
     * @param board The board to check.
     * @param coordinates The position of the last-placed mark.
     * @return An [Optional] containing the [Winner] if found, or
     *   [Optional.empty] if there is no winner.
     */
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

    /**
     * Scans all rows for a full line of identical non-zero marks.
     *
     * @param board The board to scan.
     * @return A [Winner] if a complete row is found, or `null`.
     */
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

    /**
     * Checks only the row intersecting the given [coordinates] for
     * a full line.
     *
     * @param board The board to check.
     * @param coordinates Position whose row is checked.
     * @return A [Winner] if the row is complete, or `null`.
     */
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

    /**
     * Scans all columns for a full line of identical non-zero marks.
     *
     * @param board The board to scan.
     * @return A [Winner] if a complete column is found, or `null`.
     */
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

    /**
     * Checks only the column intersecting the given [coordinates] for
     * a full line.
     *
     * @param board The board to check.
     * @param coordinates Position whose column is checked.
     * @return A [Winner] if the column is complete, or `null`.
     */
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

    /**
     * Checks both diagonals for a full line of identical non-zero marks.
     *
     * First checks the main diagonal (top-left to bottom-right),
     * then the anti-diagonal (top-right to bottom-left).
     *
     * @param board The board to check.
     * @return A [Winner] if either diagonal is complete, or `null`.
     */
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
