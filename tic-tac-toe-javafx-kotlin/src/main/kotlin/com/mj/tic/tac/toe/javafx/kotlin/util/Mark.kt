package com.mj.tic.tac.toe.javafx.kotlin.util

/**
 * Represents a single mark placed on the game board during a move.
 *
 * This data class pairs the visual character displayed on the UI button
 * ('X' or 'O') with the board [Coordinates] where the mark was placed.
 * It is returned by [MarkCellTask] as the result of a successful move
 * and consumed by [PlayAreaPanelController.onTaskSucceeded] to determine
 * the next game state.
 *
 * @property symbol The character representing the mark on the UI
 *   ('X' for the first player, 'O' for the second).
 * @property coordinates The zero-based board position of this mark.
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 25-01-2023
 */

data class Mark(val symbol: Char, val coordinates: Coordinates)
