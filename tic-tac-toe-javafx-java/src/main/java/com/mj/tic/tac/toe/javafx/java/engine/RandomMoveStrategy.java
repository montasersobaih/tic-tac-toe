package com.mj.tic.tac.toe.javafx.java.engine;

import com.mj.tic.tac.toe.javafx.java.util.Board;
import com.mj.tic.tac.toe.javafx.java.util.Coordinates;
import com.mj.tic.tac.toe.javafx.java.util.Player;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * A move strategy that selects a random empty cell from the board.
 * <p>
 * This is the simplest possible AI strategy — it ignores the opponent's
 * position and makes no attempt to win or block. It picks uniformly at
 * random from all currently available (empty) cells. Despite its
 * naivety, this strategy is useful as:
 * <ul>
 *   <li>A placeholder AI for early development and testing.</li>
 *   <li>The computer's "easy" difficulty level.</li>
 *   <li>A baseline to compare against more sophisticated strategies.</li>
 * </ul>
 *
 * <h2>Edge cases</h2>
 * <ul>
 *   <li>If the board is full (no empty cells), {@link Optional#empty()}
 *       is returned — the caller is responsible for checking the game
 *       state before invoking this strategy.</li>
 *   <li>The {@code player} parameter is accepted for interface
 *       compatibility but is <strong>not used</strong> in the decision;
 *       random play is indifferent to which side is moving.</li>
 * </ul>
 *
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 30-05-2026
 */
public final class RandomMoveStrategy implements MoveStrategy {

    /**
     * Shared source of randomness used to pick a random index from the
     * list of available cells.
     */
    private final Random random = new Random();

    /**
     * Finds a move by picking a random empty cell from the board.
     *
     * @param board  the current game board, expected to have at least one
     *               empty cell.
     * @param player the player about to move (ignored by this strategy).
     * @return an {@link Optional} containing a random {@link Coordinates}
     * from the set of empty cells, or {@link Optional#empty()} if
     * the board is full.
     */
    @Override
    public Optional<Coordinates> findMove(Board board, Player player) {
        List<Coordinates> moves = board.getEmptyCells();

        if (moves.isEmpty()) {
            return Optional.empty();
        }

        var randomIndex = random.nextInt(moves.size());
        return Optional.of(moves.get(randomIndex));
        //        return Optional.of(moves)
        //                .map(List::size)
        //                .filter(size -> size > 0)
        //                .map(random::nextInt)
        //                .map(moves::get)
        //                .or(Optional::empty);
    }
}
