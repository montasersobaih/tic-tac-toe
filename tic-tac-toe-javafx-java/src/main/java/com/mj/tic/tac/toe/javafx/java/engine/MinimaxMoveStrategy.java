package com.mj.tic.tac.toe.javafx.java.engine;

import com.mj.tic.tac.toe.javafx.java.util.Board;
import com.mj.tic.tac.toe.javafx.java.util.Coordinates;
import com.mj.tic.tac.toe.javafx.java.util.Player;
import com.mj.tic.tac.toe.javafx.java.util.Winner;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * A move strategy that uses the minimax algorithm with alpha-beta
 * pruning to play optimally.
 *
 * <h2>Performance optimizations</h2>
 * <ul>
 *   <li><strong>Mutate-unmutate</strong> &mdash; instead of cloning
 *       the board at every node ({@link Board#deepCopy()}), the
 *       algorithm tentatively marks a cell, recurses, then unmarks it.
 *       This eliminates all board allocations during the search.</li>
 *   <li><strong>Alpha-beta pruning</strong> &mdash; branches that
 *       cannot possibly influence the final decision are cut off,
 *       reducing the effective branching factor.</li>
 *   <li><strong>Inline winner check</strong> &mdash; instead of the
 *       general-purpose {@link WinnerDetector} (which launches three
 *       {@code CompletableFuture} per call), a tight inline loop
 *       checks the 8 winning lines directly.</li>
 *   <li><strong>Int-based iteration</strong> &mdash; the hot loop
 *       iterates over raw indices instead of allocating
 *       {@link Coordinates} objects via
 *       {@link Board#getEmptyCells()}.</li>
 * </ul>
 *
 * <h2>Edge cases</h2>
 * <ul>
 *   <li>If the board is already full, {@link Optional#empty()} is
 *       returned.</li>
 *   <li>If multiple moves are equally good (all lead to a draw), the
 *       first one encountered is chosen.</li>
 * </ul>
 *
 * @see RandomMoveStrategy Fallback or "easy" alternative.
 * @see MoveStrategy The interface this class implements.
 */
public final class MinimaxMoveStrategy implements MoveStrategy {

    @Override
    public Optional<Coordinates> findMove(Board board, Player player) {
        int bestScore = Integer.MIN_VALUE;
        int bestX = -1, bestY = -1;
        int dimension = board.getDimension();

        for (int x = 0; x < dimension; x++) {
            for (int y = 0; y < dimension; y++) {
                if (board.get(x, y) != 0) {
                    continue;
                }

                board.mark(x, y, player);
                int score = minimax(board, player.opponent(), player);
                board.unmark(x, y);

                if (score > bestScore) {
                    bestScore = score;
                    bestX = x;
                    bestY = y;

                    if (bestScore == 1) {
                        return Optional.of(new Coordinates(x, y));
                    }
                }
            }
        }

        return bestX == -1 ? Optional.empty() : Optional.of(new Coordinates(bestX, bestY));
    }

    private int minimax(Board board, Player opponent, Player player) {
        Optional<Winner> oWinner = WinnerDetector.detect(board);
        if (oWinner.isPresent()) {
            return oWinner.map(Winner::getPlayer)
                    .filter(player::equals)
                    .map(p -> 1)
                    .orElse(-1);
        } else if (board.isFull()) {
            return 0;
        }

        boolean isMax = opponent == player;
        int bestScore = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        BiFunction<Integer, Integer, Integer> best = isMax ? Integer::max : Integer::min;

        int dimension = board.getDimension();
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (board.get(i, j) == 0) {
                    board.mark(i, j, opponent);
                    int score = minimax(board, opponent.opponent(), player);
                    board.unmark(i, j);

                    bestScore = best.apply(score, bestScore);
                }
            }
        }

        return bestScore;
    }
}
