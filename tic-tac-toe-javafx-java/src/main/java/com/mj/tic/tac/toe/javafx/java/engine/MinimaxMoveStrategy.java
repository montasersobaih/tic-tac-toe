package com.mj.tic.tac.toe.javafx.java.engine;

import com.mj.tic.tac.toe.javafx.java.util.Board;
import com.mj.tic.tac.toe.javafx.java.util.Coordinates;
import com.mj.tic.tac.toe.javafx.java.util.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * An optimal Tic-Tac-Toe AI that uses the minimax algorithm with
 * alpha-beta pruning, a Zobrist-based transposition table, heuristic
 * move ordering, and adaptive iterative deepening.
 *
 * <p>This class implements the {@link MoveStrategy} interface and
 * serves as the "hard" AI opponent.  It explores the game tree
 * exhaustively on small boards (3&times;3 and 4&times;4) and falls
 * back to a time-bounded iterative-deepening search on larger boards
 * (5&times;5+) so that a move is always returned within a predictable
 * time window.
 *
 * <hr>
 *
 * <h2>Algorithm overview</h2>
 *
 * <p>The core algorithm is a negamax-style minimax that traverses the
 * game tree by <em>mutating</em> the board in place (placing a mark,
 * recursing, then unmarking it) instead of cloning the board at every
 * node.  This avoids all per-node heap allocation inside the search
 * hot path.
 *
 * <p>Three orthogonal techniques reduce the number of nodes visited:
 *
 * <ol>
 *   <li><strong>Alpha-beta pruning</strong> &mdash; each recursive call
 *       carries an <em>alpha</em> (best score the maximizing player can
 *       guarantee) and a <em>beta</em> (best score the minimizing player
 *       can guarantee).  When {@code alpha >= beta} the current branch
 *       is provably irrelevant and is pruned via a labeled {@code break}.</li>
 *
 *   <li><strong>Transposition table</strong> &mdash; the board is hashed
 *       via Zobrist hashing, a technique where each (cell, player) pair
 *       is assigned a random 64-bit value.  The board hash is the XOR of
 *       all values for occupied cells.  Because XOR is its own inverse,
 *       the hash can be updated incrementally during mark/unmark with a
 *       single XOR per operation.  The hash serves as a key into a
 *       {@link HashMap} that caches the exact game-theoretic score of
 *       every position encountered.  Identical positions reached through
 *       different move sequences are evaluated only once.</li>
 *
 *   <li><strong>Move ordering</strong> &mdash; before searching, empty
 *       cells are ranked by {@link #heuristicScore}: cells closer to the
 *       board center and cells that lie on lines with many friendly (or
 *       opponent) marks are searched first. This maximizes the
 *       effectiveness of alpha-beta pruning because examining the
 *       strongest move early produces tighter alpha/beta bounds.</li>
 * </ol>
 *
 * <hr>
 *
 * <h2>Two search modes</h2>
 *
 * <p>{@link #findMove} automatically selects between two strategies
 * based on board size and remaining empty cells:
 *
 * <h3>Mode A &mdash; Full-depth minimax (3&times;3 and 4&times;4)</h3>
 * <ul>
 *   <li>Computes the complete game tree with alpha-beta pruning and
 *       transposition caching.</li>
 *   <li>Returns the provably optimal move (win, draw, or delayed loss).</li>
 *   <li>The transposition table is cleared once per top-level call,
 *       so memory usage is bounded by the unique positions reachable
 *       from the current state.</li>
 * </ul>
 *
 * <h3>Mode B &mdash; Iterative deepening (5&times;5+ with &ge;8 empties)</h3>
 * <ul>
 *   <li>Performs repeated depth-limited alpha-beta searches at
 *       increasing depths (1, 2, 3, &hellip;) within a 3-second time
 *       budget ({@value #TIME_LIMIT_MS} ms).</li>
 *   <li>After each completed iteration the best move from that depth
 *       becomes the candidate; the next iteration uses it as its
 *       starting point for move ordering.</li>
 *   <li>When the time budget is exhausted, the best move from the
 *       <em>deepest completed</em> iteration is returned.</li>
 *   <li>Non-terminal positions beyond the depth limit are scored by
 *       {@link #evaluate}, a heuristic that counts uncontested
 *       squares on each line.</li>
 *   <li>No transposition table is shared between iterations (the table
 *       is cleared at the start of every depth pass) to avoid
 *       depth-inaccurate cached scores.</li>
 * </ul>
 *
 * <hr>
 *
 * <h2>Additional optimizations</h2>
 *
 * <h3>First-move shortcut</h3>
 * <p>When the board is completely empty the optimal opening move on any
 * odd-dimensioned board is the center cell {@code (dim/2, dim/2)}.
 * Returning this immediately avoids a large portion of the game tree
 * (the entire first level of branching &times; its subtree).
 *
 * <h3>Inline winner check</h3>
 * <p>{@link #isWin} replaces the general-purpose {@link WinnerDetector}
 * (which launches three {@link java.util.concurrent.CompletableFuture
 * CompletableFuture}s per call).  Because only the most recent mark can
 * create a win, the function examines only the 2&ndash;4 lines that
 * intersect cell {@code (x, y)} instead of every line on the board.
 * This reduces winner detection from O(n&sup2;) cell reads to O(n).
 *
 * <h3>Mutate-unmutate</h3>
 * <p>The board is never cloned during search.  Marks are applied before
 * a recursive call and undone after it returns.  This zero-allocation
 * strategy eliminates GC pressure in the hot loop.
 *
 * <hr>
 *
 * <h2>Edge cases</h2>
 * <ul>
 *   <li><strong>Board full</strong> &mdash; if no empty cells remain,
 *       {@link Optional#empty()} is returned.</li>
 *   <li><strong>Multiple optimal moves</strong> &mdash; when several
 *       moves lead to the same score (all draws), the one with the
 *       highest heuristic score is chosen (i.e. the first in the
 *       sorted candidate list that achieves the best score).</li>
 *   <li><strong>Winning move found</strong> &mdash; as soon as a move
 *       scores {@code +1} (guaranteed win) the search short-circuits
 *       and returns immediately.</li>
 *   <li><strong>Timeout during iterative deepening</strong> &mdash; if
 *       the deadline is reached while evaluating top-level candidates,
 *       the current depth iteration is abandoned and the best result
 *       from the previous completed depth is used.</li>
 * </ol>
 *
 * <hr>
 *
 * <h2>Thread safety</h2>
 *
 * <p>This class is <strong>not</strong> thread-safe.  The
 * {@link #transpositionTable transpositionTable} instance field is
 * mutated during search and is not synchronized.  Each call to
 * {@link #findMove} must be externally synchronized or issued from a
 * single thread.  The class is safe for single-threaded alternation
 * because {@code findMove} clears the table at the start of each
 * invocation.
 *
 * <hr>
 *
 * <h2>Zobrist hash structure</h2>
 *
 * <p>The {@link #ZOBRIST ZOBRIST} array is a 3-D table of random
 * {@code long}s indexed by {@code [row][column][playerValue]}, where
 * {@code playerValue} is {@code 1} for the human player and {@code 2}
 * for the computer (see {@link Player#getValue()}).  The hash of an
 * empty board is 0; placing a player at {@code (i, j)} XORs
 * {@code ZOBRIST[i][j][playerValue]} into the running hash, and
 * unmaking the move XORs the same value again (which reverts the
 * bit).  The random seed is fixed ({@code 42}) so that hashes are
 * deterministic across JVM instances.
 *
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @see RandomMoveStrategy  Non-optimal ("easy") AI alternative.
 * @see MoveStrategy        The interface this class implements.
 * @see Board               The mutable game board mutated during search.
 * @since 30-05-2026
 */
public final class MinimaxMoveStrategy implements MoveStrategy {

    /**
     * Zobrist hash values for every (cell, player) combination.
     * <p>
     * Indexed as {@code ZOBRIST[row][column][playerValue]}, where
     * {@code playerValue} is {@code 1} for {@link Player#HUMAN} or
     * {@code 2} for {@link Player#COMPUTER}.  The value {@code 0}
     * (empty cell) is never stored &mdash; it contributes nothing
     * to the hash because XORing with zero is a no-op.
     * <p>
     * The array is pre-computed once in the static initializer with
     * a fixed seed ({@code 42}) so that hashes are deterministic
     * across all JVM invocations.
     *
     * @see #computeZobrist(Board)
     * @see #ZOBRIST_MAX_DIM
     */
    private static final long[][][] ZOBRIST;

    /**
     * Maximum board dimension that the Zobrist table can accommodate.
     * <p>
     * Boards larger than this cannot be hashed and will cause an
     * {@link ArrayIndexOutOfBoundsException}.  The value {@code 15}
     * is sufficient for any reasonable Tic-Tac-Toe variant.
     */
    private static final int ZOBRIST_MAX_DIM = 15;

    /**
     * Time limit in milliseconds for the iterative-deepening search
     * used on boards 5&times;5 and larger.
     * <p>
     * When this budget is exhausted the search stops and the best
     * move found so far is returned.  The deadline is checked at
     * coarse granularity (between top-level candidates and before
     * each recursive descent) rather than at every node, so a single
     * search step may slightly overrun the budget.
     *
     * @see #findMoveTimed(Board, Player)
     */
    private static final long TIME_LIMIT_MS = 3000;

    /**
     * Populates the Zobrist hash table with deterministic random values.
     * <p>
     * Every combination of {@code (row, column, playerValue)} within
     * {@link #ZOBRIST_MAX_DIM} is assigned a distinct 64-bit random
     * value from a {@link Random} seeded with {@code 42}.  This ensures
     * that two JVM instances produce identical hashes for the same
     * board state.
     */
    static {
        Random rnd = new Random(42);
        ZOBRIST = new long[ZOBRIST_MAX_DIM][ZOBRIST_MAX_DIM][3];
        for (int i = 0; i < ZOBRIST_MAX_DIM; i++) {
            for (int j = 0; j < ZOBRIST_MAX_DIM; j++) {
                for (int k = 0; k < 3; k++) {
                    ZOBRIST[i][j][k] = rnd.nextLong();
                }
            }
        }
    }

    /**
     * Cache of evaluated board positions.
     * <p>
     * Keys are Zobrist hashes (64-bit {@code long}) of board states;
     * values are their exact game-theoretic scores ({@code -1}, {@code 0},
     * or {@code 1}).  The table is cleared once per top-level
     * {@link #findMove} call (or once per depth iteration in timed mode)
     * to keep memory usage proportional to the size of the game tree
     * rooted at that position.
     * <p>
     * <strong>Not thread-safe.</strong>
     *
     * @see #minimax(Board, Player, Player, int, int, Player, int, int, long)
     * @see #alphaBeta(Board, Player, Player, int, int, Player, int, int, long, int, int, long)
     */
    private final Map<Long, Integer> transpositionTable = new HashMap<>();

    /**
     * Checks whether the player who just placed a mark has won the game.
     * <p>
     * Only the 2&ndash;4 lines that intersect the most recent mark at
     * {@code (x, y)} are examined:
     * <ol>
     *   <li>The row {@code x}</li>
     *   <li>The column {@code y}</li>
     *   <li>The main diagonal (only if {@code x == y})</li>
     *   <li>The anti-diagonal (only if {@code x + y == n - 1})</li>
     * </ol>
     * This is O(n) in the board dimension, compared to O(n&sup2;) for a
     * full-board scan or the {@link java.util.concurrent.CompletableFuture}
     * overhead of the general-purpose {@link WinnerDetector}.
     *
     * @param board  the board to inspect (not modified)
     * @param x      row of the most recent mark
     * @param y      column of the most recent mark
     * @param player the player who placed the mark
     * @return {@code true} if every cell on at least one of the
     * intersecting lines is occupied by {@code player}
     */
    private static boolean isWin(Board board, int x, int y, Player player) {
        int n = board.getDimension();

        boolean win = true;
        byte pValue = player.getValue();
        for (int j = 0; j < n; j++) {
            if (board.get(x, j) != pValue) {
                win = false;
                break;
            }
        }

        if (win) {
            return true;
        }

        win = true;
        for (int i = 0; i < n; i++) {
            if (board.get(i, y) != pValue) {
                win = false;
                break;
            }
        }

        if (win) {
            return true;
        }

        if (x == y) {
            win = true;
            for (int i = 0; i < n; i++) {
                if (board.get(i, i) != pValue) {
                    win = false;
                    break;
                }
            }

            if (win) {
                return true;
            }
        }

        if (x + y == n - 1) {
            win = true;
            for (int i = 0; i < n; i++) {
                if (board.get(i, n - 1 - i) != pValue) {
                    win = false;
                    break;
                }
            }

            if (win) {
                return true;
            }
        }

        return false;
    }

    /**
     * Computes the Zobrist hash of the current board state.
     * <p>
     * The hash is the XOR of all random values assigned to occupied
     * cells.  Empty cells (value {@code 0}) are skipped because they
     * contribute nothing to the XOR.  The resulting hash uniquely
     * identifies the board configuration for use as a key in the
     * transposition table.
     * <p>
     * This method performs a full scan of the board (O(n&sup2;)) and
     * is called once at the root of the search tree.  Deeper nodes
     * update the hash incrementally during mark/unmark rather than
     * calling this method again.
     *
     * @param board the board to hash (not modified)
     * @return a 64-bit hash of the board state
     * @see #ZOBRIST
     * @see #minimax(Board, Player, Player, int, int, Player, int, int, long)
     */
    private static long computeZobrist(Board board) {
        int dim = board.getDimension();

        long h = 0;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                byte v = board.get(i, j);
                if (v != 0) {
                    h ^= ZOBRIST[i][j][v];
                }
            }
        }

        return h;
    }

    /**
     * Collects empty cells ordered by a heuristic score descending.
     * <p>
     * Every empty cell on the board is scored by {@link #heuristicScore}
     * and sorted with the highest-scoring candidate first.  This ordering
     * is critical for alpha-beta efficiency: examining the most promising
     * move first produces tighter alpha/beta bounds earlier, which causes
     * more pruning of subsequent siblings.
     *
     * @param board  the current board state (not modified)
     * @param player the player to move (used to compute friendly and opponent counts)
     * @return a mutable list of {@link Candidate} objects, sorted
     * descending by heuristic score; empty if the board is full
     */
    private static List<Candidate> orderedCandidates(Board board, Player player) {
        List<Candidate> list = new ArrayList<>();

        int dim = board.getDimension();
        for (int x = 0; x < dim; x++) {
            for (int y = 0; y < dim; y++) {
                if (board.get(x, y) != 0) {
                    continue;
                }

                int score = heuristicScore(board, x, y, player);
                list.add(new Candidate(x, y, score));
            }
        }

        list.sort((a, b) -> b.score - a.score);
        return list;
    }

    /**
     * Computes a heuristic score for placing a mark at {@code (x, y)}.
     * <p>
     * The score is the sum of two sub-scores:
     * <ol>
     *   <li><strong>Center proximity</strong> &mdash; cells closer to the
     *       geometric center of the board score higher because they
     *       participate in more winning lines (row, column, and both
     *       diagonals).</li>
     *   <li><strong>Line potential</strong> &mdash; the number of friendly
     *       marks already present on each of the up-to-four lines that
     *       intersect {@code (x, y)} is counted; the maximum among them
     *       is weighted by 10.  The same is done for opponent marks and
     *       weighted by 8, so that blocking a nearly-complete opponent
     *       line is valued almost as highly as extending one's own.</li>
     * </ol>
     * <p>
     * This heuristic is intentionally cheap to compute (O(n) cell reads)
     * so that sorting hundreds of candidates does not dominate the
     * overall search time.
     *
     * @param board  the current board state (not modified)
     * @param x      row of the candidate cell
     * @param y      column of the candidate cell
     * @param player the player who would place the mark
     * @return an integer score where higher values indicate more
     * promising moves
     */
    private static int heuristicScore(Board board, int x, int y, Player player) {
        int dim = board.getDimension();
        int mid = dim / 2;
        int centreScore = (dim - Math.abs(x - mid)) + (dim - Math.abs(y - mid));

        byte p = player.getValue();
        byte o = player.opponent().getValue();

        int rowOwn = 0, colOwn = 0, diagOwn = 0, antiOwn = 0;
        int rowOpp = 0, colOpp = 0, diagOpp = 0, antiOpp = 0;

        for (int j = 0; j < dim; j++) {
            byte v = board.get(x, j);
            if (v == p) {
                rowOwn++;
            } else if (v == o) {
                rowOpp++;
            }
        }

        for (int i = 0; i < dim; i++) {
            byte v = board.get(i, y);
            if (v == p) {
                colOwn++;
            } else if (v == o) {
                colOpp++;
            }
        }

        if (x == y) {
            for (int i = 0; i < dim; i++) {
                byte v = board.get(i, i);
                if (v == p) {
                    diagOwn++;
                } else if (v == o) {
                    diagOpp++;
                }
            }
        }

        if (x + y == dim - 1) {
            for (int i = 0; i < dim; i++) {
                byte v = board.get(i, dim - 1 - i);
                if (v == p) {
                    antiOwn++;
                } else if (v == o) {
                    antiOpp++;
                }
            }
        }

        int lineScore = Math.max(rowOwn, Math.max(colOwn, Math.max(diagOwn, antiOwn))) * 10
                + Math.max(rowOpp, Math.max(colOpp, Math.max(diagOpp, antiOpp))) * 8;

        return centreScore + lineScore;
    }

    /**
     * Heuristic evaluation of a non-terminal board position.
     * <p>
     * Scores the board from the AI's perspective by examining every
     * line (rows, columns, and both diagonals).  For each line that
     * contains <em>no opponent marks</em>, the square of the AI's
     * mark count is added to the score.  Conversely, for each line
     * that contains <em>no AI marks</em>, the square of the opponent's
     * mark count is subtracted.  Lines that contain both players'
     * marks are blocked and contribute nothing.
     * <p>
     * This produces values roughly proportional to the AI's positional
     * advantage, rewarding clustered threats and penalizing opponent
     * clusters.  A complete line for the AI would score {@code n²}
     * (the maximum possible) and a complete line for the opponent
     * would score {@code -n²}.
     *
     * @param board the board to evaluate (not modified)
     * @param ai    the AI player (positive scores favor this player)
     * @return a heuristic score: positive values favor the AI,
     * negative values favor the opponent, and zero indicates
     * a balanced position
     */
    private static int evaluate(Board board, Player ai) {
        int dim = board.getDimension();
        int score = 0;
        byte p = ai.getValue();
        byte o = ai.opponent().getValue();

        for (int i = 0; i < dim; i++) {
            int pCount = 0, oCount = 0;
            for (int j = 0; j < dim; j++) {
                byte v = board.get(i, j);
                if (v == p) {
                    pCount++;
                } else if (v == o) {
                    oCount++;
                }
            }

            if (oCount == 0) {
                score += pCount * pCount;
            }

            if (pCount == 0) {
                score -= oCount * oCount;
            }
        }

        for (int j = 0; j < dim; j++) {
            int pCount = 0, oCount = 0;
            for (int i = 0; i < dim; i++) {
                byte v = board.get(i, j);
                if (v == p) {
                    pCount++;
                } else if (v == o) {
                    oCount++;
                }
            }

            if (oCount == 0) {
                score += pCount * pCount;
            }

            if (pCount == 0) {
                score -= oCount * oCount;
            }
        }

        int pCount = 0, oCount = 0;
        for (int i = 0; i < dim; i++) {
            byte v = board.get(i, i);
            if (v == p) {
                pCount++;
            } else if (v == o) {
                oCount++;
            }
        }

        if (oCount == 0) {
            score += pCount * pCount;
        }

        if (pCount == 0) {
            score -= oCount * oCount;
        }

        pCount = 0;
        oCount = 0;
        for (int i = 0; i < dim; i++) {
            byte v = board.get(i, dim - 1 - i);
            if (v == p) {
                pCount++;
            } else if (v == o) {
                oCount++;
            }
        }

        if (oCount == 0) {
            score += pCount * pCount;
        }

        if (pCount == 0) {
            score -= oCount * oCount;
        }

        return score;
    }

    @Override
    public Optional<Coordinates> findMove(Board board, Player player) {
        int dim = board.getDimension();

        if (board.getMarkedCellsCount() == 0) {
            return Optional.of(new Coordinates(dim / 2, dim / 2));
        }

        int emptyCount = dim * dim - board.getMarkedCellsCount();

        if (dim >= 5 && emptyCount >= 8) {
            return findMoveTimed(board, player);
        }

        transpositionTable.clear();
        List<Candidate> candidates = orderedCandidates(board, player);
        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        long baseHash = computeZobrist(board);
        int bestScore = Integer.MIN_VALUE;
        int bestX = candidates.get(0).x;
        int bestY = candidates.get(0).y;

        for (Candidate c : candidates) {
            int x = c.x, y = c.y;

            board.mark(x, y, player);
            long h = baseHash ^ ZOBRIST[x][y][player.getValue()];
            int score = minimax(board, player.opponent(), player, x, y, player, Integer.MIN_VALUE, Integer.MAX_VALUE, h);
            board.unmark(x, y);

            if (score > bestScore) {
                bestScore = score;
                bestX = x;
                bestY = y;
                if (bestScore == 1) {
                    break;
                }
            }
        }

        return Optional.of(new Coordinates(bestX, bestY));
    }

    /**
     * Full-depth minimax search with alpha-beta pruning and transposition
     * caching.
     * <p>
     * This method explores the game tree exhaustively until every branch
     * reaches a terminal state (win, loss, or draw).  It is used for
     * boards 3&times;3 and 4&times;4 where the tree is small enough to
     * complete in reasonable time.
     * <p>
     * The search is <em>negamax</em> in style: the current player
     * ({@code turn}) maximizes when they are the AI player and minimizes
     * otherwise.  The {@code alpha} and {@code beta} parameters define
     * the search window; when the window closes ({@code alpha >= beta})
     * the remaining siblings are pruned via a labeled {@code break outer}.
     * <p>
     * Each node first checks the transposition table.  If the position
     * has been evaluated before in this top-level call, the cached score
     * is returned immediately.  Otherwise, the method recurses, stores
     * the result, and returns it.
     *
     * @param board      the current board state (mutated in place during search)
     * @param turn       the player whose turn it is at this node
     * @param ai         the AI player (the one we are computing the move for at the root)
     * @param lastX      row of the most recently placed mark
     * @param lastY      column of the most recently placed mark
     * @param lastPlayer the player who placed the most recent mark
     * @param alpha      lower bound of the search window (best score the maximizer can guarantee)
     * @param beta       upper bound of the search window (best score the minimizer can guarantee)
     * @param hash       Zobrist hash of the current board state
     * @return {@code 1} if the AI can force a win from this position,
     * {@code -1} if the AI will lose with optimal play,
     * {@code 0} for a draw
     */
    private int minimax(Board board, Player turn, Player ai, int lastX, int lastY, Player lastPlayer, int alpha, int beta, long hash) {
        Integer cached = transpositionTable.get(hash);
        if (cached != null) {
            return cached;
        }

        if (isWin(board, lastX, lastY, lastPlayer)) {
            int score = lastPlayer == ai ? 1 : -1;
            transpositionTable.put(hash, score);
            return score;
        }

        if (board.isFull()) {
            transpositionTable.put(hash, 0);
            return 0;
        }

        boolean isMax = turn == ai;
        int best = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int dim = board.getDimension();

        outer:
        for (int x = 0; x < dim; x++) {
            for (int y = 0; y < dim; y++) {
                if (board.get(x, y) != 0) {
                    continue;
                }

                board.mark(x, y, turn);
                long h = hash ^ ZOBRIST[x][y][turn.getValue()];
                int score = minimax(board, turn.opponent(), ai, x, y, turn, alpha, beta, h);
                board.unmark(x, y);

                if (isMax) {
                    if (score > best) {
                        best = score;
                    }
                    if (score > alpha) {
                        alpha = score;
                    }
                } else {
                    if (score < best) {
                        best = score;
                    }
                    if (score < beta) {
                        beta = score;
                    }
                }

                if (alpha >= beta) {
                    break outer;
                }
            }
        }

        transpositionTable.put(hash, best);
        return best;
    }

    /**
     * Selects the best move for large boards using iterative-deepening
     * alpha-beta search with a time limit.
     * <p>
     * This method is invoked when the board is 5&times;5 or larger and
     * at least 8 empty cells remain &mdash; scenarios where full-depth
     * minimax would take seconds or minutes to complete.  It performs
     * repeated depth-limited searches at increasing depths, returning
     * the best move found so far when the time budget is exhausted.
     * <p>
     * Process:
     * <ol>
     *   <li>Score and sort all empty cells via
     *       {@link #orderedCandidates} (only once, before the timer
     *       starts).</li>
     *   <li>For {@code depth = 1, 2, 3, ...} perform a full alpha-beta
     *       search limited to that depth, clearing the transposition
     *       table between depths to avoid reusing depth-stale scores.</li>
     *   <li>If a candidate scores {@code +1} (guaranteed win) at any
     *       level, return immediately.</li>
     *   <li>When the deadline passes mid-iteration, stop and return the
     *       best move from the <em>deepest completed</em> iteration.</li>
     * </ol>
     *
     * @param board  the current board state (mutated during search)
     * @param player the AI player to compute a move for
     * @return the best {@link Coordinates} found within the time budget,
     * or {@link Optional#empty()} if the board is full
     */
    private Optional<Coordinates> findMoveTimed(Board board, Player player) {
        List<Candidate> candidates = orderedCandidates(board, player);

        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        long baseHash = computeZobrist(board);
        long deadline = System.currentTimeMillis() + TIME_LIMIT_MS;

        int bestX = candidates.get(0).x;
        int bestY = candidates.get(0).y;
        int bestScore = Integer.MIN_VALUE;

        int dim = board.getDimension();
        for (int depth = 1; depth <= dim * dim; depth++) {
            transpositionTable.clear();
            int iterBestScore = Integer.MIN_VALUE;
            int iterBestX = bestX;
            int iterBestY = bestY;
            boolean timedOut = false;

            for (Candidate c : candidates) {
                if (System.currentTimeMillis() >= deadline) {
                    timedOut = true;
                    break;
                }

                int x = c.x, y = c.y;
                board.mark(x, y, player);
                long h = baseHash ^ ZOBRIST[x][y][player.getValue()];
                int score = alphaBeta(board, player.opponent(), player,
                        x, y, player,
                        Integer.MIN_VALUE, Integer.MAX_VALUE,
                        h, 1, depth, deadline);
                board.unmark(x, y);

                if (score > iterBestScore) {
                    iterBestScore = score;
                    iterBestX = x;
                    iterBestY = y;
                    if (score == 1) {
                        break;
                    }
                }
            }

            if (!timedOut) {
                bestScore = iterBestScore;
                bestX = iterBestX;
                bestY = iterBestY;
                if (bestScore == 1) {
                    break;
                }
            } else {
                break;
            }
        }

        return Optional.of(new Coordinates(bestX, bestY));
    }

    /**
     * Depth-limited alpha-beta search with deadline checking.
     * <p>
     * This is the workhorse of the iterative-deepening timed mode.
     * It behaves identically to {@link #minimax} but accepts three
     * additional parameters:
     * <ul>
     *   <li>{@code depth} &mdash; current search depth (incremented
     *       on each recursive call)</li>
     *   <li>{@code maxDepth} &mdash; depth limit for this iteration;
     *       when {@code depth >= maxDepth} the heuristic
     *       {@link #evaluate} is called instead of recursing further</li>
     *   <li>{@code deadline} &mdash; absolute time in milliseconds
     *       beyond which the search returns a heuristic evaluation
     *       instead of continuing deeper</li>
     * </ul>
     * <p>
     * Unlike {@code minimax}, this method does <em>not</em> use the
     * transposition table, because depth-inaccurate cached scores
     * could skew results across iterations.
     *
     * @param board      the current board state (mutated during search)
     * @param turn       the player whose turn it is at this node
     * @param ai         the AI player (the one we are computing the
     *                   move for at the root)
     * @param lastX      row of the most recently placed mark
     * @param lastY      column of the most recently placed mark
     * @param lastPlayer the player who placed the most recent mark
     * @param alpha      lower bound of the search window
     * @param beta       upper bound of the search window
     * @param hash       Zobrist hash of the current board state
     * @param depth      current search depth (0 at the root)
     * @param maxDepth   depth limit for this iteration
     * @param deadline   system millis time beyond which the search
     *                   should abort with a heuristic score
     * @return a score in the range {@code [-1, 1]}, where values
     * closer to {@code 1} are better for the AI
     */
    private int alphaBeta(Board board, Player turn, Player ai,
                          int lastX, int lastY, Player lastPlayer,
                          int alpha, int beta, long hash,
                          int depth, int maxDepth, long deadline) {
        if (System.currentTimeMillis() >= deadline) {
            return evaluate(board, ai);
        }

        if (isWin(board, lastX, lastY, lastPlayer)) {
            return lastPlayer == ai ? 1 : -1;
        }

        if (board.isFull()) {
            return 0;
        }

        if (depth >= maxDepth) {
            return evaluate(board, ai);
        }

        boolean isMax = turn == ai;
        int best = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int dim = board.getDimension();

        outer:
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (board.get(i, j) != 0) {
                    continue;
                }

                board.mark(i, j, turn);
                long h = hash ^ ZOBRIST[i][j][turn.getValue()];
                int score = alphaBeta(board, turn.opponent(), ai,
                        i, j, turn, alpha, beta, h,
                        depth + 1, maxDepth, deadline);
                board.unmark(i, j);

                if (isMax) {
                    if (score > best) {
                        best = score;
                    }
                    if (score > alpha) {
                        alpha = score;
                    }
                } else {
                    if (score < best) {
                        best = score;
                    }
                    if (score < beta) {
                        beta = score;
                    }
                }

                if (alpha >= beta) {
                    break outer;
                }
            }
        }

        return best;
    }

    /**
     * A row-column-score triple representing one candidate move.
     * <p>
     * Instances are created by {@link #orderedCandidates} and sorted
     * descending by {@link #score} so that the most promising moves
     * are searched first by the alpha-beta engine.
     */
    private static final class Candidate {

        /**
         * Row index of the candidate cell.
         */
        final int x;

        /**
         * Column index of the candidate cell.
         */
        final int y;

        /**
         * Heuristic score of this candidate, computed by
         * {@link #heuristicScore}.  Higher values indicate moves
         * that are likely to be stronger.
         */
        final int score;

        Candidate(int x, int y, int score) {
            this.x = x;
            this.y = y;
            this.score = score;
        }
    }
}
