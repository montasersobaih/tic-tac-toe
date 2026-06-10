package com.mj.tic.tac.toe.javafx.java.engine;

import com.mj.tic.tac.toe.javafx.java.util.Board;
import com.mj.tic.tac.toe.javafx.java.util.Coordinates;
import com.mj.tic.tac.toe.javafx.java.util.Player;
import java.util.Optional;

/**
 * Strategy interface for selecting a move in a tic-tac-toe game.
 * <p>
 * Implementations encapsulate different AI decision algorithms,
 * ranging from random selection to minimax-based optimal play.
 * Strategies are expected to be stateless or internally stateful
 * but should be safe for single-threaded use.
 *
 * <h2>Contract</h2>
 * <ul>
 *   <li>Implementations must <strong>not</strong> modify the board.</li>
 *   <li>If no move is available (board is full), the method must
 *       return {@link Optional#empty()}.</li>
 *   <li>The returned {@link Coordinates} (if present) must correspond
 *       to a cell that is empty at the time of the call.</li>
 * </ul>
 *
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @see RandomMoveStrategy
 * @see Coordinates
 * @see Board
 * @since 30-05-2026
 */
public interface MoveStrategy {

    /**
     * Computes a move for the given player on the specified board.
     *
     * @param board  the current game state; must not be {@code null}.
     * @param player the player who is about to move; must not be
     *               {@code null}.
     * @return an {@link Optional} containing the chosen
     * {@link Coordinates}, or {@link Optional#empty()} if no
     * empty cells remain.
     */
    Optional<Coordinates> findMove(Board board, Player player);
}
