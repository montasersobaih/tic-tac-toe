package com.mj.tic.tac.toe.javafx.kotlin.engine

import com.mj.tic.tac.toe.javafx.kotlin.engine.MinimaxMoveStrategy.Companion.TIME_LIMIT_MS
import com.mj.tic.tac.toe.javafx.kotlin.engine.MinimaxMoveStrategy.Companion.ZOBRIST_MAX_DIM
import com.mj.tic.tac.toe.javafx.kotlin.engine.MinimaxMoveStrategy.Companion.evaluate
import com.mj.tic.tac.toe.javafx.kotlin.util.Board
import com.mj.tic.tac.toe.javafx.kotlin.util.Coordinates
import com.mj.tic.tac.toe.javafx.kotlin.util.Player
import java.util.Optional
import kotlin.random.Random

/**
 * AI move strategy using the minimax algorithm with alpha-beta pruning.
 *
 * This is the most advanced AI strategy in the application, used for
 * [Difficulty.HARD] and partly for [Difficulty.MEDIUM]. It combines
 * several optimization techniques to compute strong moves efficiently:
 *
 * - **Minimax search** with full game-tree exploration (for standard 3x3
 *   boards, the search is complete and therefore optimal).
 * - **Alpha-beta pruning** to eliminate irrelevant branches and reduce
 *   the effective branching factor.
 * - **Zobrist hashing** for a transposition table, caching evaluated
 *   board states to avoid redundant computation across different move
 *   orderings.
 * - **Move ordering** via heuristic scoring (center proximity + line
 *   potential) to improve alpha-beta pruning efficiency.
 * - **Iterative deepening** with a 3-second time limit for large boards
 *   (dimension >= 5), using a depth-limited alpha-beta search that
 *   returns the best move found within the allotted time.
 *
 * The evaluation function is designed for arbitrary odd-dimensioned
 * boards and scores positions based on the density of marks in each
 * row, column, and diagonal.
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

class MinimaxMoveStrategy : MoveStrategy {

    /**
     * Internal data class representing a candidate move with its
     * heuristic score, used for move ordering.
     *
     * @property x The row index of the candidate cell.
     * @property y The column index of the candidate cell.
     * @property score The heuristic priority score (higher = more promising).
     */
    private data class Candidate(val x: Int, val y: Int, val score: Int)

    /**
     * Transposition table mapping Zobrist hash -> evaluation score.
     *
     * Caches the minimax evaluation for previously visited board states
     * to avoid redundant computation. The hash is computed incrementally
     * during search for efficiency.
     */
    private val transpositionTable = mutableMapOf<Long, Int>()

    /**
     * Finds the best move for the given player using minimax search.
     *
     * Entry-point behavior depends on board state:
     * - **Empty board:** Returns the center cell (optimal first move).
     * - **Large board (dim >= 5, >= 8 empty):** Delegates to
     *   [findMoveTimed] for iterative deepening with time limit.
     * - **Standard case:** Performs full minimax with alpha-beta pruning,
     *   transposition table, and candidate ordering. Exits early on a
     *   guaranteed winning move (score = 1).
     *
     * @param board The current game board state.
     * @param player The player to compute a move for (typically [Player.COMPUTER]).
     * @return An [Optional] containing the best [Coordinates] found,
     *   or [Optional.empty] if no moves are available.
     */
    override fun findMove(board: Board, player: Player): Optional<Coordinates> {
        val dim = board.dimension

        if (board.markedCellsCount == 0) {
            return dim.div(2).let { Coordinates(it, it) }.let { Optional.of(it) }
        }

        val emptyCount = dim.times(dim).minus(board.markedCellsCount)

        if (dim >= 5 && emptyCount >= 8) {
            return findMoveTimed(board, player)
        }

        transpositionTable.clear()
        val candidates = orderedCandidates(board, player)
        if (candidates.isEmpty()) {
            return Optional.empty()
        }

        val baseHash = computeZobrist(board)
        var bestScore = Int.MIN_VALUE
        var bestX = candidates[0].x
        var bestY = candidates[0].y

        for (c in candidates) {
            val x = c.x
            val y = c.y

            board.mark(x, y, player)
            val h = baseHash xor ZOBRIST[x][y][player.value.toInt()]
            val score = minimax(board, player.opponent(), player, x, y, player, Int.MIN_VALUE, Int.MAX_VALUE, h)
            board.unmark(x, y)

            if (score > bestScore) {
                bestScore = score
                bestX = x
                bestY = y
                if (bestScore == 1) {
                    break
                }
            }
        }

        return Optional.of(Coordinates(bestX, bestY))
    }

    /**
     * Iterative deepening search with a 3-second wall-clock time limit.
     *
     * For large boards where full-depth minimax is infeasible, this
     * method runs depth-limited alpha-beta search at increasing depths
     * (1, 2, 3, ...). It stops deepening when:
     * - The time limit ([TIME_LIMIT_MS]) is reached.
     * - A winning move (score = 1) is found.
     *
     * Returns the best move found at the deepest successfully completed
     * depth level.
     *
     * @param board The current game board state.
     * @param player The player to compute a move for.
     * @return An [Optional] containing the best [Coordinates] found
     *   within the time limit.
     */
    private fun findMoveTimed(board: Board, player: Player): Optional<Coordinates> {
        val candidates = orderedCandidates(board, player)

        if (candidates.isEmpty()) {
            return Optional.empty()
        }

        val baseHash = computeZobrist(board)
        val deadline = System.currentTimeMillis() + TIME_LIMIT_MS

        var bestX = candidates[0].x
        var bestY = candidates[0].y
        var bestScore = Int.MIN_VALUE

        var depth = 1
        val dimension = board.dimension.times(board.dimension)
        while (depth <= dimension) {
            transpositionTable.clear()
            var iterBestScore = Int.MIN_VALUE
            var iterBestX = bestX
            var iterBestY = bestY
            var timedOut = false

            for (c in candidates) {
                if (System.currentTimeMillis() >= deadline) {
                    timedOut = true
                    break
                }

                val x = c.x
                val y = c.y
                board.mark(x, y, player)
                val h = baseHash xor ZOBRIST[x][y][player.value.toInt()]
                val score = alphaBeta(
                    board, player.opponent(), player,
                    x, y, player,
                    Int.MIN_VALUE, Int.MAX_VALUE, h, 1, depth, deadline
                )
                board.unmark(x, y)

                if (score > iterBestScore) {
                    iterBestScore = score
                    iterBestX = x
                    iterBestY = y
                    if (score == 1) {
                        break
                    }
                }
            }

            if (!timedOut) {
                bestScore = iterBestScore
                bestX = iterBestX
                bestY = iterBestY
                if (bestScore == 1) {
                    break
                }
            } else {
                break
            }

            depth++
        }

        return Optional.of(Coordinates(bestX, bestY))
    }

    /**
     * Recursive minimax with alpha-beta pruning and transposition table.
     *
     * Explores the full game tree (no depth limit) to compute the
     * exact minimax value of the current position. Returns:
     * - `1` if the AI player wins.
     * - `-1` if the opponent wins.
     * - `0` for a draw.
     *
     * Uses iterative alpha-beta window narrowing and breaks out of
     * the search loop when a cutoff occurs.
     *
     * @param board The board state to evaluate.
     * @param turn The player whose turn it is at this node.
     * @param ai The AI player (used to determine which side is "max").
     * @param lastX Row of the last move made.
     * @param lastY Column of the last move made.
     * @param lastPlayer The player who made the last move.
     * @param alpha Alpha value for pruning (best max score so far).
     * @param beta Beta value for pruning (best min score so far).
     * @param hash Zobrist hash of the current board state.
     * @return The minimax score: 1 (win), 0 (draw), -1 (loss).
     */
    private fun minimax(
        board: Board, turn: Player, ai: Player,
        lastX: Int, lastY: Int, lastPlayer: Player,
        alpha: Int, beta: Int, hash: Long
    ): Int {
        val cached = transpositionTable[hash]
        if (cached != null) {
            return cached
        }

        if (isWin(board, lastX, lastY, lastPlayer)) {
            val score = if (lastPlayer == ai) 1 else -1
            transpositionTable[hash] = score
            return score
        }

        if (board.isFull()) {
            transpositionTable[hash] = 0
            return 0
        }

        val isMax = turn == ai
        var best = if (isMax) Int.MIN_VALUE else Int.MAX_VALUE
        val dim = board.dimension
        var currentAlpha = alpha
        var currentBeta = beta

        outer@
        for (x in 0 until dim) {
            for (y in 0 until dim) {
                if (board.get(x, y) != 0.toByte()) {
                    continue
                }

                board.mark(x, y, turn)
                val h = hash xor ZOBRIST[x][y][turn.value.toInt()]
                val score = minimax(board, turn.opponent(), ai, x, y, turn, currentAlpha, currentBeta, h)
                board.unmark(x, y)

                if (isMax) {
                    if (score > best) {
                        best = score
                    }
                    if (score > currentAlpha) {
                        currentAlpha = score
                    }
                } else {
                    if (score < best) {
                        best = score
                    }
                    if (score < currentBeta) {
                        currentBeta = score
                    }
                }

                if (currentAlpha >= currentBeta) {
                    break@outer
                }
            }
        }

        transpositionTable[hash] = best
        return best
    }

    /**
     * Depth-limited alpha-beta search used by iterative deepening.
     *
     * At leaf nodes (when [depth] >= [maxDepth] or the board is terminal),
     * uses the [evaluate] heuristic instead of searching deeper. Also
     * checks the [deadline] timestamp at each node for early timeout,
     * returning the heuristic evaluation immediately if time has run out.
     *
     * @param board The board state to evaluate.
     * @param turn The player whose turn it is.
     * @param ai The AI player.
     * @param lastX Row of the last move.
     * @param lastY Column of the last move.
     * @param lastPlayer The player who made the last move.
     * @param alpha Alpha value for pruning.
     * @param beta Beta value for pruning.
     * @param hash Zobrist hash of the current board state.
     * @param depth Current search depth (1-indexed from root).
     * @param maxDepth Maximum search depth to explore.
     * @param deadline Timestamp (millis) after which search should time out.
     * @return The evaluated score for this position.
     */
    private fun alphaBeta(
        board: Board, turn: Player, ai: Player,
        lastX: Int, lastY: Int, lastPlayer: Player,
        alpha: Int, beta: Int, hash: Long,
        depth: Int, maxDepth: Int, deadline: Long
    ): Int {
        if (System.currentTimeMillis() >= deadline) {
            return evaluate(board, ai)
        }

        if (isWin(board, lastX, lastY, lastPlayer)) {
            return if (lastPlayer == ai) 1 else -1
        }

        if (board.isFull()) {
            return 0
        }

        if (depth >= maxDepth) {
            return evaluate(board, ai)
        }

        val isMax = turn == ai
        var best = if (isMax) Int.MIN_VALUE else Int.MAX_VALUE
        val dim = board.dimension
        var currentAlpha = alpha
        var currentBeta = beta

        outer@
        for (i in 0 until dim) {
            for (j in 0 until dim) {
                if (board.get(i, j) != 0.toByte()) {
                    continue
                }

                board.mark(i, j, turn)
                val h = hash xor ZOBRIST[i][j][turn.value.toInt()]
                val score = alphaBeta(
                    board, turn.opponent(), ai,
                    i, j, turn, currentAlpha, currentBeta, h,
                    depth + 1, maxDepth, deadline
                )
                board.unmark(i, j)

                if (isMax) {
                    if (score > best) {
                        best = score
                    }
                    if (score > currentAlpha) {
                        currentAlpha = score
                    }
                } else {
                    if (score < best) {
                        best = score
                    }
                    if (score < currentBeta) {
                        currentBeta = score
                    }
                }

                if (currentAlpha >= currentBeta) {
                    break@outer
                }
            }
        }

        return best
    }

    companion object {

        /**
         * Maximum board dimension supported by the pre-computed
         * Zobrist random value table.
         */
        private const val ZOBRIST_MAX_DIM = 15

        /**
         * Maximum search time in milliseconds for iterative deepening.
         * When the deadline is reached, the search returns the best
         * move found so far at the deepest completed depth.
         */
        private const val TIME_LIMIT_MS = 3000L

        /**
         * Pre-computed table of random 64-bit values for Zobrist hashing.
         *
         * Initialised once with a fixed seed (42) for reproducibility.
         * Dimensions: [ZOBRIST_MAX_DIM] x [ZOBRIST_MAX_DIM] x 3
         * (3 states per cell: empty=0, human=1, computer=2).
         */
        private val ZOBRIST: Array<Array<LongArray>> = run {
            val rnd = Random(42)
            Array(ZOBRIST_MAX_DIM) {
                Array(ZOBRIST_MAX_DIM) {
                    LongArray(3) { rnd.nextLong() }
                }
            }
        }

        /**
         * Checks whether placing a mark at (x, y) completes a winning line.
         *
         * Examines four potential lines through the given cell:
         * - The row at index [x].
         * - The column at index [y].
         * - The main diagonal (top-left to bottom-right), if (x, y) lies on it.
         * - The anti-diagonal (top-right to bottom-left), if (x, y) lies on it.
         *
         * @param board The board to check.
         * @param x Row index of the last-placed mark.
         * @param y Column index of the last-placed mark.
         * @param player The player who placed the mark.
         * @return `true` if the mark completes a full line for the player.
         */
        private fun isWin(board: Board, x: Int, y: Int, player: Player): Boolean {
            val n = board.dimension
            val pValue = player.value

            var win = true
            for (j in 0 until n) {
                if (board.get(x, j) != pValue) {
                    win = false
                    break
                }
            }
            if (win) return true

            win = true
            for (i in 0 until n) {
                if (board.get(i, y) != pValue) {
                    win = false
                    break
                }
            }
            if (win) return true

            if (x == y) {
                win = true
                for (i in 0 until n) {
                    if (board.get(i, i) != pValue) {
                        win = false
                        break
                    }
                }
                if (win) return true
            }

            if (x + y == n - 1) {
                win = true
                for (i in 0 until n) {
                    if (board.get(i, n - 1 - i) != pValue) {
                        win = false
                        break
                    }
                }
                if (win) return true
            }

            return false
        }

        /**
         * Computes the Zobrist hash for the entire board state.
         *
         * XORs the pre-computed random values for each occupied cell
         * (empty cells contribute nothing). The resulting 64-bit hash
         * uniquely identifies the board state for transposition table
         * lookups.
         *
         * @param board The board to hash.
         * @return A 64-bit hash uniquely representing the board state.
         */
        private fun computeZobrist(board: Board): Long {
            val dim = board.dimension
            var h = 0L
            for (i in 0 until dim) {
                for (j in 0 until dim) {
                    val v = board.get(i, j).toInt()
                    if (v != 0) {
                        h = h xor ZOBRIST[i][j][v]
                    }
                }
            }
            return h
        }

        /**
         * Returns all empty cells sorted descending by heuristic score.
         *
         * Move ordering significantly improves alpha-beta pruning
         * efficiency by examining the most promising moves first,
         * leading to earlier cutoffs.
         *
         * @param board The board to analyze.
         * @param player The player whose moves are being ordered.
         * @return A list of [Candidate] objects sorted by descending score.
         */
        private fun orderedCandidates(board: Board, player: Player): List<Candidate> {
            val list = mutableListOf<Candidate>()
            val dim = board.dimension
            for (x in 0 until dim) {
                for (y in 0 until dim) {
                    if (board.get(x, y) != 0.toByte()) continue
                    val score = heuristicScore(board, x, y, player)
                    list.add(Candidate(x, y, score))
                }
            }
            list.sortByDescending { it.score }
            return list
        }

        /**
         * Computes a heuristic score for a candidate cell.
         *
         * The score combines two factors:
         * - **Center proximity:** Cells closer to the board center
         *   score higher (important for larger boards).
         * - **Line potential:** Counts own marks (×10) and opponent
         *   marks (×8) in the row, column, and diagonals crossing
         *   the candidate cell. Higher values mean the cell is part
         *   of a more valuable line.
         *
         * @param board The board to analyze.
         * @param x Row index of the candidate.
         * @param y Column index of the candidate.
         * @param player The player whose perspective to score from.
         * @return An integer score (higher = more promising move).
         */
        private fun heuristicScore(board: Board, x: Int, y: Int, player: Player): Int {
            val dim = board.dimension
            val mid = dim / 2
            val centreScore = (dim - kotlin.math.abs(x - mid)) + (dim - kotlin.math.abs(y - mid))

            val p = player.value
            val o = player.opponent().value

            var rowOwn = 0
            var colOwn = 0
            var diagOwn = 0
            var antiOwn = 0
            var rowOpp = 0
            var colOpp = 0
            var diagOpp = 0
            var antiOpp = 0

            for (j in 0 until dim) {
                val v = board.get(x, j)
                if (v == p) rowOwn++
                else if (v == o) rowOpp++
            }

            for (i in 0 until dim) {
                val v = board.get(i, y)
                if (v == p) colOwn++
                else if (v == o) colOpp++
            }

            if (x == y) {
                for (i in 0 until dim) {
                    val v = board.get(i, i)
                    if (v == p) diagOwn++
                    else if (v == o) diagOpp++
                }
            }

            if (x + y == dim - 1) {
                for (i in 0 until dim) {
                    val v = board.get(i, dim - 1 - i)
                    if (v == p) antiOwn++
                    else if (v == o) antiOpp++
                }
            }

            val lineScore = maxOf(rowOwn, colOwn, diagOwn, antiOwn) * 10 +
                    maxOf(rowOpp, colOpp, diagOpp, antiOpp) * 8

            return centreScore + lineScore
        }

        /**
         * Heuristic evaluation function for non-terminal positions.
         *
         * Scores each row, column, and diagonal on the board:
         * - If **only the AI** has marks on the line: adds the square
         *   of the AI's mark count (favors lines the AI dominates).
         * - If **only the opponent** has marks on the line: subtracts
         *   the square of the opponent's mark count (penalizes lines
         *   the opponent dominates).
         * - If both players have marks on the line: contributes nothing
         *   (the line is blocked for both sides).
         *
         * @param board The board to evaluate.
         * @param ai The AI player (the side being scored).
         * @return An integer score. Positive = favourable for AI,
         *   negative = favourable for opponent.
         */
        private fun evaluate(board: Board, ai: Player): Int {
            val dim = board.dimension
            var score = 0
            val p = ai.value
            val o = ai.opponent().value

            for (i in 0 until dim) {
                var pCount = 0
                var oCount = 0
                for (j in 0 until dim) {
                    val v = board.get(i, j)
                    if (v == p) pCount++
                    else if (v == o) oCount++
                }
                if (oCount == 0) score += pCount * pCount
                if (pCount == 0) score -= oCount * oCount
            }

            for (j in 0 until dim) {
                var pCount = 0
                var oCount = 0
                for (i in 0 until dim) {
                    val v = board.get(i, j)
                    if (v == p) pCount++
                    else if (v == o) oCount++
                }
                if (oCount == 0) score += pCount * pCount
                if (pCount == 0) score -= oCount * oCount
            }

            var pCount = 0
            var oCount = 0
            for (i in 0 until dim) {
                val v = board.get(i, i)
                if (v == p) pCount++
                else if (v == o) oCount++
            }
            if (oCount == 0) score += pCount * pCount
            if (pCount == 0) score -= oCount * oCount

            pCount = 0
            oCount = 0
            for (i in 0 until dim) {
                val v = board.get(i, dim - 1 - i)
                if (v == p) pCount++
                else if (v == o) oCount++
            }
            if (oCount == 0) score += pCount * pCount
            if (pCount == 0) score -= oCount * oCount

            return score
        }
    }
}
