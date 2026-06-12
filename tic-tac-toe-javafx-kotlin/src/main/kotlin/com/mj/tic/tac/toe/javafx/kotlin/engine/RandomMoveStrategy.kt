package com.mj.tic.tac.toe.javafx.kotlin.engine

import com.mj.tic.tac.toe.javafx.kotlin.util.Board
import com.mj.tic.tac.toe.javafx.kotlin.util.Coordinates
import com.mj.tic.tac.toe.javafx.kotlin.util.Player
import java.util.Optional
import kotlin.random.Random

/**
 * AI move strategy that picks a uniformly random empty cell.
 *
 * This is the simplest possible AI strategy and is used for the
 * [Difficulty.EASY] level. It makes no attempt to evaluate board
 * position or block the opponent. The strategy is also sometimes
 * selected randomly by the [Difficulty.MEDIUM] level to create
 * an unpredictable mix of random and optimal moves.
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

class RandomMoveStrategy : MoveStrategy {

    /**
     * Selects a random empty cell from the board.
     *
     * Retrieves all empty cells via [Board.getEmptyCells] and picks
     * one uniformly at random using [kotlin.random.Random.nextInt].
     *
     * @param board The current game board state.
     * @param player The player making the move (ignored by this strategy).
     * @return An [Optional] containing the randomly chosen [Coordinates],
     *   or [Optional.empty] if no empty cells remain.
     */
    override fun findMove(board: Board, player: Player): Optional<Coordinates> {
        val moves = board.getEmptyCells()

        if (moves.isEmpty()) {
            return Optional.empty()
        }

        val randomIndex = Random.nextInt(moves.size)
        return Optional.of(moves[randomIndex])
    }
}
