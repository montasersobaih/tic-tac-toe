package com.mj.tic.tac.toe.javafx.kotlin.engine

import com.mj.tic.tac.toe.javafx.kotlin.util.Board
import com.mj.tic.tac.toe.javafx.kotlin.util.Coordinates
import com.mj.tic.tac.toe.javafx.kotlin.util.Player
import java.util.Optional
import kotlin.random.Random

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

class MinimaxMoveStrategy : MoveStrategy {

    private data class Candidate(val x: Int, val y: Int, val score: Int)

    private val transpositionTable = mutableMapOf<Long, Int>()

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

        private const val ZOBRIST_MAX_DIM = 15

        private const val TIME_LIMIT_MS = 3000L

        private val ZOBRIST: Array<Array<LongArray>> = run {
            val rnd = Random(42)
            Array(ZOBRIST_MAX_DIM) {
                Array(ZOBRIST_MAX_DIM) {
                    LongArray(3) { rnd.nextLong() }
                }
            }
        }

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
