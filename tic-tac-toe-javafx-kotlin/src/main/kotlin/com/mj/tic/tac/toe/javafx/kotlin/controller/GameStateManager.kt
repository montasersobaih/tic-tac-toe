package com.mj.tic.tac.toe.javafx.kotlin.controller

import com.mj.tic.tac.toe.javafx.kotlin.util.GameState
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import javafx.application.Platform

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

class GameStateManager {

    private val subscribers = ConcurrentHashMap<GameState, CopyOnWriteArrayList<Subscriber<*>>>()

    fun <T> subscribe(state: GameState, subscriber: Subscriber<T>) {
        subscribers.computeIfAbsent(state) { CopyOnWriteArrayList() }.add(subscriber)
    }

    fun <T> subscribe(state: GameState, vararg subscribers: Subscriber<T>) {
        for (subscriber in subscribers) {
            this.subscribe(state, subscriber)
        }
    }

    fun <T> unSubscribe(state: GameState, subscriber: Subscriber<T>): Boolean {
        val stateSubscribers = subscribers[state] ?: return false
        return stateSubscribers.remove(subscriber)
    }

    fun <T> unSubscribe(state: GameState, vararg subscribers: Subscriber<T>) {
        subscribers.forEach { this.unSubscribe(state, it) }
    }

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
