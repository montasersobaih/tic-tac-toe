package com.mj.tic.tac.toe.javafx.kotlin.util

/**
 * Represents the lifecycle states of a game session.
 *
 * These states are published through [GameStateManager] to all
 * registered [Subscriber] instances. Each state carries an optional
 * typed payload delivered to subscribers via the publish method.
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

enum class GameState {
    /**
     * Published when the game is fully reset.
     *
     * The board is cleared but score counters are preserved.
     * Payload: none (null).
     */
    RESET_GAME,

    /**
     * Published when a new game begins with a chosen difficulty.
     *
     * Payload: [Difficulty] — the difficulty level chosen by the player.
     */
    NEW_GAME,

    /**
     * Published when the game reaches a terminal state.
     *
     * Payload: [Player]? — the winning player ([Player.HUMAN] or
     * [Player.COMPUTER]), or null if the game ended in a draw.
     */
    GAME_OVER
}
