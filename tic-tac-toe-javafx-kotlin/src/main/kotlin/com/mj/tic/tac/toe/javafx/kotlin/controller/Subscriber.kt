package com.mj.tic.tac.toe.javafx.kotlin.controller

import com.mj.tic.tac.toe.javafx.kotlin.util.GameState

/**
 * Functional interface for receiving game-state change notifications
 * through the [GameStateManager] publish-subscribe system.
 *
 * Implementations register themselves for specific [GameState] values
 * via [GameStateManager.subscribe] and are notified when that state
 * is published. The optional payload carries additional context about
 * the event (e.g., the chosen [Difficulty] for [GameState.NEW_GAME],
 * or the winning [Player] for [GameState.GAME_OVER]).
 *
 * Defined as a Kotlin [fun interface] (SAM), allowing both class-based
 * implementations (e.g., [LeftPanelController], [PlayAreaPanelController])
 * and lambda-based usage.
 *
 * @param T The type of the payload value expected by this subscriber.
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

fun interface Subscriber<T> {

    /**
     * Called when a subscribed game state is published.
     *
     * @param state The [GameState] that was published.
     * @param value The optional payload associated with the event.
     *   For [GameState.NEW_GAME], this is a [Difficulty].
     *   For [GameState.GAME_OVER], this is a [Player]? (null = draw).
     *   For [GameState.RESET_GAME], this is null.
     */
    fun update(state: GameState, value: T?)
}
