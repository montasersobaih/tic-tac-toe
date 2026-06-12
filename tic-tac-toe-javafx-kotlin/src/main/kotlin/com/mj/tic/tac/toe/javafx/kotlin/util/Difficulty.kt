package com.mj.tic.tac.toe.javafx.kotlin.util

/**
 * Represents the AI difficulty level selected by the player.
 *
 * Each constant determines which [MoveStrategy] implementation the
 * AI uses to compute its moves:
 * - [EASY]: always uses [RandomMoveStrategy] (purely random moves).
 * - [MEDIUM]: randomly alternates between [RandomMoveStrategy] and
 *   [MinimaxMoveStrategy] with equal probability (50/50).
 * - [HARD]: always uses [MinimaxMoveStrategy] for optimal play.
 *
 * @property resourceKey The i18n resource bundle key used to look up
 *   the localized display name for this difficulty level.
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

enum class Difficulty(val resourceKey: String) {
    /** AI picks random moves using [RandomMoveStrategy]. */
    EASY("control.label.difficulty.easy"),

    /** AI randomly alternates between random and minimax strategies. */
    MEDIUM("control.label.difficulty.medium"),

    /** AI uses full minimax with alpha-beta pruning via [MinimaxMoveStrategy]. */
    HARD("control.label.difficulty.hard");

    /**
     * Returns the i18n resource key for this difficulty level.
     *
     * @return The [resourceKey] string used for localized label lookups.
     */
    override fun toString(): String = resourceKey
}
