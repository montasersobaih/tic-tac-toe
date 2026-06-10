package com.mj.tic.tac.toe.javafx.java.controller;

import com.mj.tic.tac.toe.javafx.java.util.GameState;

/**
 * A functional contract for components that wish to be notified of
 * {@link GameState} transitions published by the {@link GameStateManager}.
 *
 * <p>Implementors register themselves via
 * {@link GameStateManager#subscribe(GameState, Subscriber)} and are called
 * back on the JavaFX Application Thread whenever the corresponding state is
 * published. The single method {@link #update(GameState, Object)} receives both
 * the state that was published and an optional typed payload carrying
 * context-specific data.</p>
 *
 * <p>This interface is intentionally generic in {@code T} so that subscribers
 * can declare the exact payload type they expect without casting. For example:
 * <ul>
 *   <li>{@code Subscriber<Difficulty>} &mdash; expects a
 *       {@link com.mj.tic.tac.toe.javafx.java.util.Difficulty Difficulty}
 *       payload (e.g. for {@code NEW_GAME}).</li>
 *   <li>{@code Subscriber<Object>} &mdash; accepts heterogeneous payloads
 *       and performs its own type dispatch (e.g. for controllers that handle
 *       multiple states).</li>
 * </ul>
 * </p>
 *
 * <p>Controllers that need to observe one or more game states typically
 * implement this interface and register themselves during their
 * {@code initialize()} phase via the shared
 * {@link com.mj.tic.tac.toe.javafx.java.controller.ControllerMediator
 * ControllerMediator}.</p>
 *
 * <p>This is a <strong>functional interface</strong> and can be used with
 * lambdas or method references where convenient:
 * <pre>{@code
 * gameStateManager.subscribe(GameState.RESET_GAME,
 *     (state, value) -> boardPane.reset());
 * }</pre>
 * </p>
 *
 * @param <T> the type of the payload carried with the state notification
 * @author Montaser Jamal
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @created 29-05-2026
 * @see GameStateManager
 * @see GameState
 * @since 1.0.0
 */

@FunctionalInterface
public interface Subscriber<T> {

    /**
     * Called by the {@link GameStateManager} when the subscribed
     * {@link GameState} is published.
     *
     * <p>Implementors should use the {@code state} parameter to determine
     * what action to take and the {@code value} parameter for any
     * associated data. The method is always invoked on the JavaFX
     * Application Thread, so UI updates can be performed directly
     * without additional {@link javafx.application.Platform#runLater}
     * calls.</p>
     *
     * @param state the game state that was published (never {@code null})
     * @param value the typed payload associated with the transition, or
     *              {@code null} if the state carries no data
     */
    void update(GameState state, T value);
}
