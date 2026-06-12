package com.mj.tic.tac.toe.javafx.kotlin.util

/**
 * Represents a position on the Tic Tac Toe game board.
 *
 * This data class provides a zero-based (row, column) coordinate pair used
 * throughout the application for all board cell lookups, move computations,
 * and win-detection logic. Being a Kotlin data class, it automatically
 * provides structural equality, [hashCode], [toString], [copy], and
 * destructuring declarations.
 *
 * @property x The row index on the board (0-based). The first dimension
 *   of the board's internal 2D array.
 * @property y The column index on the board (0-based). The second
 *   dimension of the board's internal 2D array.
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 25-01-2023
 */

data class Coordinates(val x: Int, val y: Int)
