package com.mj.tic.tac.toe.javafx.java.task;

import com.mj.tic.tac.toe.javafx.java.engine.MinimaxMoveStrategy;
import com.mj.tic.tac.toe.javafx.java.engine.MoveStrategy;
import com.mj.tic.tac.toe.javafx.java.engine.RandomMoveStrategy;
import com.mj.tic.tac.toe.javafx.java.util.Board;
import com.mj.tic.tac.toe.javafx.java.util.Coordinates;
import com.mj.tic.tac.toe.javafx.java.util.Difficulty;
import com.mj.tic.tac.toe.javafx.java.util.Player;
import java.util.Optional;
import java.util.Random;

/**
 * A background {@link javafx.concurrent.Task} that computes the
 * computer's next move on a separate thread, keeping the JavaFX
 * UI thread responsive during AI evaluation.
 *
 * <h2>Purpose</h2>
 * Tic-tac-toe AI move computation, particularly the minimax
 * algorithm used at {@link Difficulty#HARD}, can traverse up to
 * 9! (362 880) game states in the worst case. Running this on the
 * JavaFX Application Thread would cause visible UI stutter or
 * freezes. This task delegates the computation to a background
 * thread and communicates the result back to the UI thread through
 * the standard JavaFX {@code Task} callback mechanism
 * ({@link #succeeded}, {@link #failed}, etc.).
 *
 * <h2>Difficulty-based strategy selection</h2>
 * The strategy used depends on the chosen difficulty:
 * <ul>
 *   <li><strong>{@link Difficulty#EASY}</strong> — Always picks a
 *       random empty cell using {@link RandomMoveStrategy}. Fast
 *       and predictable, ideal for novice players.</li>
 *   <li><strong>{@link Difficulty#MEDIUM}</strong> — Flips a fair
 *       coin (50/50) between {@link RandomMoveStrategy} and
 *       {@link MinimaxMoveStrategy} on each turn. The resulting
 *       behavior oscillates between random blunders and optimal
 *       play, creating an unpredictable opponent that is harder
 *       to exploit than a purely random one.</li>
 *   <li><strong>{@link Difficulty#HARD}</strong> — Always plays
 *       optimally using the full minimax algorithm via
 *       {@link MinimaxMoveStrategy}. The computer never loses
 *       (it either wins or draws), making this level effectively
 *       unbeatable for a perfect-information game like
 *       tic-tac-toe.</li>
 * </ul>
 *
 * <h2>Thread safety</h2>
 * Instances are <strong>not</strong> thread-safe and are intended
 * for single-use only. Each task should be created fresh, submitted
 * to a {@link javafx.concurrent.Service} or a
 * {@link java.util.concurrent.ExecutorService}, and discarded after
 * completion. Reusing an instance after it has been run is undefined
 * behavior.
 *
 * <h2>Usage example</h2>
 * <pre>{@code
 * ComputerMoveTask task = new ComputerMoveTask(board, Difficulty.HARD, Player.X);
 * task.setOnSucceeded(event -> {
 *     Optional<Coordinates> move = task.getValue();
 *     move.ifPresent(coords -> applyMoveToUI(coords));
 * });
 * new Thread(task).start();
 * }</pre>
 *
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 30-05-2026
 */

public final class ComputerMoveTask extends BaseTask<Optional<Coordinates>> {

    /**
     * Strategy instance that selects a random legal move from the
     * available empty cells. Used exclusively for
     * {@link Difficulty#EASY} and as one of the two strategies in
     * the {@link Difficulty#MEDIUM} coin-flip.
     *
     * @see RandomMoveStrategy
     */
    private final MoveStrategy randomMoveStrategy = new RandomMoveStrategy();

    /**
     * Strategy instance that plays optimally by evaluating all
     * possible future game states through the minimax algorithm.
     * Used exclusively for {@link Difficulty#HARD} and as one of
     * the two strategies in the {@link Difficulty#MEDIUM} coin-flip.
     *
     * @see MinimaxMoveStrategy
     */
    private final MoveStrategy minimaxMoveStrategy = new MinimaxMoveStrategy();

    /**
     * Pseudorandom number generator used solely for the
     * {@link Difficulty#MEDIUM} strategy-selection coin flip.
     * A new {@link Random} instance is created per task; no
     * attempt is made at cryptographic-quality randomness.
     */
    private final Random random = new Random();

    /**
     * Immutable snapshot of the game board at the time this task
     * was created. The task reads board state to evaluate moves
     * but never mutates it, honoring the {@link MoveStrategy}
     * contract.
     */
    private final Board board;

    /**
     * The difficulty level that governs which move strategy
     * (or combination of strategies) is used during
     * {@link #call()}. Must not be {@code null}.
     */
    private final Difficulty difficulty;

    /**
     * The player mark (either {@link Player#HUMAN} or {@link Player#COMPUTER})
     * representing the computer's side. The move computation finds
     * a move that maximizes the outcome for this player.
     */
    private final Player player;

    /**
     * Constructs a new computer-move computation task.
     * <p>
     * All parameters are captured and stored as final fields.
     * The task is ready to be submitted for execution as soon as
     * it is constructed; no additional setup is required.
     *
     * @param board      the current game board state; must not be
     *                   {@code null}.
     * @param difficulty the difficulty level that determines the
     *                   move-selection strategy; must not be
     *                   {@code null}.
     * @param player     the computer's player mark ({@link Player#HUMAN}
     *                   or {@link Player#COMPUTER}); must not be
     *                   {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     *                              (implicit, deferred until
     *                              {@link #call()} accesses the
     *                              field).
     */
    public ComputerMoveTask(Board board, Difficulty difficulty, Player player) {
        this.board = board;
        this.difficulty = difficulty;
        this.player = player;
    }

    /**
     * Computes the computer's next move on a background thread
     * by delegating to the appropriate move strategy.
     * <p>
     * The dispatch logic is as follows:
     * <ul>
     *   <li><strong>{@link Difficulty#EASY}</strong> — delegates
     *       directly to {@link #randomMoveStrategy}.</li>
     *   <li><strong>{@link Difficulty#MEDIUM}</strong> — randomly
     *       picks one of the two strategy instances
     *       ({@link #randomMoveStrategy} or
     *       {@link #minimaxMoveStrategy}) with equal probability,
     *       then delegates to the chosen one.</li>
     *   <li><strong>{@link Difficulty#HARD}</strong> — delegates
     *       directly to {@link #minimaxMoveStrategy}.</li>
     * </ul>
     * <p>
     * This method is invoked by the JavaFX concurrency framework
     * on a background thread and must not directly access or modify
     * UI components.
     *
     * @return an {@link Optional} containing the chosen
     * {@link Coordinates} if at least one empty cell is available,
     * or {@link Optional#empty()} if the board is full (draw or
     * terminal state).
     */
    @Override
    protected Optional<Coordinates> call() throws Exception {
        switch (difficulty) {
            case EASY:
                return randomMoveStrategy.findMove(board, player);
            case MEDIUM:
                MoveStrategy[] strategies = {randomMoveStrategy, minimaxMoveStrategy};
                int randomStrategyIndex = random.nextInt(strategies.length);
                return strategies[randomStrategyIndex].findMove(board, player);
            case HARD:
            default:
                return minimaxMoveStrategy.findMove(board, player);
        }
    }
}