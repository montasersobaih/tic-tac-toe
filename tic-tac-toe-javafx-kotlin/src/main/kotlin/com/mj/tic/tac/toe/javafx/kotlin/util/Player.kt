package com.mj.tic.tac.toe.javafx.kotlin.util

/**
 * Represents the two participants in a Tic Tac Toe game.
 *
 * Each player constant carries a [Byte] value used as the cell mark
 * in the [Board]'s internal 2D array:
 * - [HUMAN] stores `1` in board cells.
 * - [COMPUTER] stores `2` in board cells.
 *
 * The board's `cells` array uses `0` for empty cells, so these
 * non-zero values allow quick identification of which player owns
 * which cell during win detection and AI evaluation.
 *
 * @property value The byte value stored in the board for this
 *   player's marks (1 for HUMAN, 2 for COMPUTER).
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

enum class Player(val value: Byte) {
    /** The human player. Board marks are stored as byte value `1`. */
    HUMAN(1),

    /** The computer (AI) opponent. Board marks are stored as byte value `2`. */
    COMPUTER(2);

    companion object {
        /**
         * Resolves a byte value back to the corresponding [Player].
         *
         * @param value The byte value to look up (must be `1` or `2`).
         * @return [HUMAN] if value is `1`, [COMPUTER] if value is `2`.
         * @throws IllegalArgumentException if the value does not match
         *   any known player.
         */
        fun from(value: Byte): Player = values().firstOrNull {
            it.value == value
        } ?: throw IllegalArgumentException("Unknown player value: $value")
    }

    /**
     * Returns the opponent of this player.
     *
     * Convenience method for swapping turns after each move.
     *
     * @return [COMPUTER] if this is [HUMAN], [HUMAN] if this is [COMPUTER].
     */
    fun opponent(): Player = if (this == HUMAN) COMPUTER else HUMAN
}
