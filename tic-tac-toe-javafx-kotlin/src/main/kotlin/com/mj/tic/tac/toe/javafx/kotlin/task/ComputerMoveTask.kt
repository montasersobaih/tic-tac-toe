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

    private val randomMoveStrategy: MoveStrategy = RandomMoveStrategy()

    private val minimaxMoveStrategy: MoveStrategy = MinimaxMoveStrategy()

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
