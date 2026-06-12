package com.mj.tic.tac.toe.javafx.kotlin.controller

import com.mj.tic.tac.toe.javafx.kotlin.util.GameState
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import javafx.application.Platform

/**
 * Thread-safe publish-subscribe event bus for game-state changes.
 *
 * Manages a registry of [Subscriber] instances organized by [GameState].
 * Publishers dispatch events to all registered subscribers, automatically
 * routing updates to the JavaFX Application Thread when necessary.
 *
 * Thread safety is achieved through:
 * - [ConcurrentHashMap] for the subscriber registry, allowing concurrent
 *   reads and writes without explicit synchronization.
 * - [CopyOnWriteArrayList] for per-state subscriber lists, providing
 *   thread-safe iteration without locks (at the cost of copy-on-write
 *   for modifications).
 * - [Platform.runLater] dispatching for publications originating from
 *   background threads.
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

class GameStateManager {

    /**
     * Maps each [GameState] to its list of registered subscribers.
     *
     * Uses concurrent collections to allow safe access from both
     * the JavaFX Application Thread and background executor threads.
     */
    private val subscribers = ConcurrentHashMap<GameState, CopyOnWriteArrayList<Subscriber<*>>>()

    /**
     * Registers a subscriber for a specific game state.
     *
     * If no subscribers are currently registered for the given state,
     * a new subscriber list is created automatically.
     *
     * @param state The [GameState] to subscribe to.
     * @param subscriber The subscriber to register.
     */
    fun <T> subscribe(state: GameState, subscriber: Subscriber<T>) {
        subscribers.computeIfAbsent(state) { CopyOnWriteArrayList() }.add(subscriber)
    }

    /**
     * Registers multiple subscribers for a specific game state.
     *
     * @param state The [GameState] to subscribe to.
     * @param subscribers Vararg of subscribers to register.
     */
    fun <T> subscribe(state: GameState, vararg subscribers: Subscriber<T>) {
        for (subscriber in subscribers) {
            this.subscribe(state, subscriber)
        }
    }

    /**
     * Removes a subscriber from a game state's notification list.
     *
     * @param state The [GameState] to unsubscribe from.
     * @param subscriber The subscriber to remove.
     * @return `true` if the subscriber was found and removed,
     *   `false` if the state has no subscribers or the subscriber
     *   wasn't registered.
     */
    fun <T> unSubscribe(state: GameState, subscriber: Subscriber<T>): Boolean {
        val stateSubscribers = subscribers[state] ?: return false
        return stateSubscribers.remove(subscriber)
    }

    /**
     * Removes multiple subscribers from a game state's notification list.
     *
     * @param state The [GameState] to unsubscribe from.
     * @param subscribers Vararg of subscribers to remove.
     */
    fun <T> unSubscribe(state: GameState, vararg subscribers: Subscriber<T>) {
        subscribers.forEach { this.unSubscribe(state, it) }
    }

    /**
     * Publishes a game state event to all registered subscribers.
     *
     * If the current thread is the JavaFX Application Thread,
     * subscriber updates are invoked directly. Otherwise, they
     * are dispatched via [Platform.runLater] to ensure safe UI
     * access from background threads.
     *
     * @param state The [GameState] to publish.
     * @param newValue The optional payload to deliver to subscribers.
     *   Typed as [T] and cast to each subscriber's expected type.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> publish(state: GameState, newValue: T? = null) {
        val stateSubscribers = subscribers[state] ?: return
        for (subscriber in stateSubscribers) {
            val update = Runnable { (subscriber as Subscriber<T>).update(state, newValue) }
            if (Platform.isFxApplicationThread()) {
                update.run()
            } else {
                Platform.runLater(update)
            }
        }
    }
}
