package com.mj.tic.tac.toe.javafx.kotlin.util

/**
 * Captures the result of a winning board configuration.
 *
 * Produced by [WinnerDetector] when a player completes a full line
 * (row, column, or diagonal) of identical marks. Consumed by
 * [GameOverTask] to highlight the winning cells on the UI and by
 * [ViewController] to publish the game result to subscribers.
 *
 * @property player The [Player] who achieved the winning line.
 * @property locations The ordered list of board [Coordinates] that
 *   form the winning row, column, or diagonal. The length of this
 *   list equals the board [Board.dimension].
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 25-01-2023
 */

data class Winner(val player: Player, val locations: List<Coordinates>)
