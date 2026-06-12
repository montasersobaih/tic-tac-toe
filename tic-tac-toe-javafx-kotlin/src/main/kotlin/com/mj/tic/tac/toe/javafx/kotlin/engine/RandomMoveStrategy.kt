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

class RandomMoveStrategy : MoveStrategy {

    override fun findMove(board: Board, player: Player): Optional<Coordinates> {
        val moves = board.getEmptyCells()

        if (moves.isEmpty()) {
            return Optional.empty()
        }

        val randomIndex = Random.nextInt(moves.size)
        return Optional.of(moves[randomIndex])
    }
}
