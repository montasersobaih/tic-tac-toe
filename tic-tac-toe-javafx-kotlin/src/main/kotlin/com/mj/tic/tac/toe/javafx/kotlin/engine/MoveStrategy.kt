package com.mj.tic.tac.toe.javafx.kotlin.engine

import com.mj.tic.tac.toe.javafx.kotlin.util.Board
import com.mj.tic.tac.toe.javafx.kotlin.util.Coordinates
import com.mj.tic.tac.toe.javafx.kotlin.util.Player
import java.util.Optional

/**
 * Functional interface for AI move-generation algorithms.
 *
 * Implementations encapsulate different strategies for choosing the
 * computer's next move on a Tic Tac Toe board. This interface is
 * defined as a Kotlin [fun interface] (SAM), allowing both class-based
 * implementations and lambda-based usage.
 *
 * The Strategy pattern is used here to decouple the AI move selection
 * logic from the task infrastructure, making it easy to add new
 * difficulty levels or move strategies without modifying existing code.
 *
 * @see RandomMoveStrategy Picks a random empty cell (used for [Difficulty.EASY]).
 * @see MinimaxMoveStrategy Uses minimax with alpha-beta pruning (used for [Difficulty.HARD]).
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

fun interface MoveStrategy {

    /**
     * Computes the best move for the given player on the current board.
     *
     * @param board The current game board state to analyze.
     * @param player The AI player making the move (typically [Player.COMPUTER]).
     * @return An [Optional] containing the chosen [Coordinates], or
     *   [Optional.empty] if no valid moves are available (board is full).
     */
    fun findMove(board: Board, player: Player): Optional<Coordinates>
}
