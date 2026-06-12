package com.mj.tic.tac.toe.javafx.java.controller;

import com.mj.tic.tac.toe.javafx.java.util.GameState;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.application.Platform;

/**
 * <p><b>Project:</b> tic-tac-toe</p>
 * <p>
 * A thread-safe publish-subscribe event bus for game state transitions in a JavaFX
 * application. Components register interest in specific {@link GameState} changes
 * and are notified asynchronously on the JavaFX Application Thread.
 *
 * <p>Thread safety is achieved through:
 * <ul>
 *   <li>{@link ConcurrentHashMap} — safe concurrent reads and writes to the subscriber map</li>
 *   <li>{@link CopyOnWriteArrayList} — safe iteration during publication without
 *       concurrent-modification exceptions</li>
 *   <li>{@link Platform#runLater(Runnable)} — all subscriber notifications are
 *       guaranteed to execute on the JavaFX Application Thread, even when
 *       {@link #publish(GameState, Object)} is called from a background thread</li>
 * </ul>
 *
 * <p>Usage pattern:
 * <pre>{@code
 * GameStateManager manager = new GameStateManager();
 *
 * // Register interest
 * manager.subscribe(GameState.RESET_GAME, value -> boardPane.reset());
 *
 * // Trigger notification
 * manager.publish(GameState.RESET_GAME, someData);
 *
 * // Remove registration
 * manager.unsubscribe(GameState.RESET_GAME, subscriber);
 * }</pre>
 *
 * @author Montaser Jamal
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @created 29-05-2026
 * @see GameState
 * @see Subscriber
 * @since 1.0.0
 */

public final class GameStateManager {

    /**
     * Maps each {@link GameState} to a thread-safe list of subscribers.
     * <ul>
     *   <li>{@link ConcurrentHashMap} permits safe concurrent reads/writes without locking.</li>
     *   <li>{@link CopyOnWriteArrayList} (created per key by {@link #subscribe}) allows safe
     *       iteration during publication even if another thread modifies the list.</li>
     * </ul>
     */
    private final Map<GameState, List<Subscriber<?>>> subscribers = new ConcurrentHashMap<>();

    /**
     * Registers a subscriber to be notified when the given {@link GameState} is
     * published.
     *
     * <p>If no subscribers are currently registered for this state, a new
     * thread-safe list is created automatically.
     *
     * @param <T>        the type of the payload the subscriber expects
     * @param state      the game state to subscribe to (must not be {@code null})
     * @param subscriber the callback to invoke when the state is published (must
     *                   not be {@code null})
     * @throws NullPointerException if either parameter is {@code null}
     */
    public <T> void subscribe(GameState state, Subscriber<T> subscriber) {
        Objects.requireNonNull(state, "state must not be null");
        Objects.requireNonNull(subscriber, "subscriber must not be null");
        subscribers.computeIfAbsent(state, ignored -> new CopyOnWriteArrayList<>()).add(subscriber);
    }

    /**
     * Registers multiple subscribers for the given {@link GameState} in a single call.
     *
     * <p>This is a convenience varargs wrapper around {@link #subscribe(GameState, Subscriber)}.
     *
     * @param <T>         the type of the payload the subscribers expect
     * @param state       the game state to subscribe to (must not be {@code null})
     * @param subscribers one or more subscribers to register (none may be {@code null})
     * @throws NullPointerException if {@code state} is {@code null} or any subscriber is {@code null}
     */
    public <T> void subscribe(GameState state, Subscriber<T>... subscribers) {
        for (Subscriber<T> subscriber : subscribers) {
            this.subscribe(state, subscriber);
        }
    }

    /**
     * Removes a previously registered subscriber for the given {@link GameState}.
     *
     * @param <T>        the type of the payload the subscriber expects
     * @param state      the game state to unsubscribe from (must not be {@code null})
     * @param subscriber the subscriber to remove (must not be {@code null})
     * @return {@code true} if the subscriber was found and removed, {@code false}
     * if no subscribers were registered for the given state or the
     * subscriber was not present
     * @throws NullPointerException if either parameter is {@code null}
     */
    public <T> boolean unSubscribe(GameState state, Subscriber<T> subscriber) {
        Objects.requireNonNull(state, "state must not be null");
        Objects.requireNonNull(subscriber, "subscriber must not be null");

        var stateSubscribers = subscribers.get(state);
        return Objects.nonNull(stateSubscribers) && stateSubscribers.remove(subscriber);
    }

    /**
     * Removes multiple subscribers for the given {@link GameState} in a single call.
     *
     * <p>This is a convenience varargs wrapper around {@link #unSubscribe(GameState, Subscriber)}.
     *
     * @param <T>         the type of the payload the subscribers expect
     * @param state       the game state to unsubscribe from (must not be {@code null})
     * @param subscribers one or more subscribers to remove (none may be {@code null})
     * @throws NullPointerException if {@code state} is {@code null} or any subscriber is {@code null}
     */
    public <T> void unSubscribe(GameState state, Subscriber<T>... subscribers) {
        for (Subscriber<T> subscriber : subscribers) {
            this.unSubscribe(state, subscriber);
        }
    }

    /**
     * Notifies all subscribers registered for the given {@link GameState} with
     * the provided value.
     *
     * <p>If the calling thread is the JavaFX Application Thread, subscribers are
     * invoked synchronously. Otherwise, each subscriber notification is
     * dispatched via {@link Platform#runLater(Runnable)} to ensure it executes
     * on the JavaFX Application Thread.
     *
     * @param <T>      the type of the payload value
     * @param state    the game state being published (must not be {@code null})
     * @param newValue the value to deliver to each subscriber
     * @throws NullPointerException if {@code state} is {@code null}
     */
    @SuppressWarnings("unchecked")
    public <T> void publish(GameState state, T newValue) {
        Objects.requireNonNull(state, "state must not be null");

        for (Subscriber<?> subscriber : subscribers.getOrDefault(state, List.of())) {
            Runnable update = () -> ((Subscriber<T>) subscriber).update(state, newValue);
            if (Platform.isFxApplicationThread()) {
                update.run();
            } else {
                Platform.runLater(update);
            }
        }
    }

    /**
     * Notifies all subscribers registered for the given {@link GameState} with a
     * {@code null} payload.
     *
     * <p>This is a convenience overload of {@link #publish(GameState, Object)} for
     * states that carry no meaningful data.
     * `
     *
     * @param state the game state being published (must not be {@code null})
     * @throws NullPointerException if {@code state} is {@code null}
     */
    public void publish(GameState state) {
        this.publish(state, null);
    }
}
