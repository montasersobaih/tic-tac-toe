package com.mj.tic.tac.toe.javafx.kotlin.task

import com.mj.tic.tac.toe.javafx.kotlin.engine.MinimaxMoveStrategy
import com.mj.tic.tac.toe.javafx.kotlin.engine.MoveStrategy
import com.mj.tic.tac.toe.javafx.kotlin.engine.RandomMoveStrategy
import com.mj.tic.tac.toe.javafx.kotlin.util.Board
import com.mj.tic.tac.toe.javafx.kotlin.util.Coordinates
import com.mj.tic.tac.toe.javafx.kotlin.util.Difficulty
import com.mj.tic.tac.toe.javafx.kotlin.util.Player
import kotlin.random.Random

/**
 * Background task that computes the AI's next move.
 *
 * The move is computed according to the selected [Difficulty] level:
 * - [Difficulty.EASY]: Picks a random empty cell via [RandomMoveStrategy].
 * - [Difficulty.MEDIUM]: Randomly alternates between [RandomMoveStrategy]
 *   and [MinimaxMoveStrategy] with equal probability (50/50).
 * - [Difficulty.HARD]: Uses [MinimaxMoveStrategy] for optimal play.
 *
 * The result is consumed by [PlayAreaPanelController.onTaskSucceeded],
 * which fires a synthetic click event on the chosen cell to trigger
 * a [MarkCellTask].
 *
 * @property board The current game board state to analyze.
 * @property difficulty The difficulty level determining the strategy.
 * @property player The AI player (typically [Player.COMPUTER]).
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

class ComputerMoveTask(
    private val board: Board,
    private val difficulty: Difficulty,
    private val player: Player
) : BaseTask<Coordinates?>() {

    /** Strategy instance for random move generation. */
    private val randomMoveStrategy: MoveStrategy = RandomMoveStrategy()

    /** Strategy instance for optimal minimax-based move generation. */
    private val minimaxMoveStrategy: MoveStrategy = MinimaxMoveStrategy()

    /**
     * Computes the AI's move based on the current difficulty.
     *
     * @return The chosen [Coordinates] for the AI's move, or null if
     *   no valid moves are available (board is full).
     */
    override fun call(): Coordinates? {
        return when (difficulty) {
            Difficulty.EASY -> randomMoveStrategy.findMove(board, player).orElse(null)
            Difficulty.MEDIUM -> {
                val strategies = arrayOf(randomMoveStrategy, minimaxMoveStrategy)
                val index = Random.nextInt(strategies.size)
                return strategies[index].findMove(board, player).orElse(null)
            }

            Difficulty.HARD -> minimaxMoveStrategy.findMove(board, player).orElse(null)
        }
    }
}
