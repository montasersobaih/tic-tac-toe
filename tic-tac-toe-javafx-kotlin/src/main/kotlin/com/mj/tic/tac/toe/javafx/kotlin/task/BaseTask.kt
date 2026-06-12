package com.mj.tic.tac.toe.javafx.kotlin.task

import javafx.concurrent.Task

/**
 * Abstract base class for all background tasks in the application.
 *
 * Extends [javafx.concurrent.Task] to provide a common supertype
 * for all game-related background operations. Subclasses implement
 * [call] to perform their work on a background thread and return
 * a result of type [V].
 *
 * Tasks are executed via the [com.mj.tic.tac.toe.javafx.kotlin.controller.ControllerMediator] (implemented by
 * [com.mj.tic.tac.toe.javafx.kotlin.controller.view.ViewController]) on a single-thread executor, ensuring sequential
 * execution and preventing concurrency issues in the game logic.
 *
 * @param V The result type produced by the task.
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

abstract class BaseTask<V> : Task<V>() {

    /**
     * Performs the background computation and returns the result.
     *
     * Called by the JavaFX concurrency framework on a background
     * thread. Subclasses must implement this method to perform
     * their specific game logic.
     *
     * @return The result of type [V], or null if applicable.
     * @throws Exception if the computation fails.
     */
    @Throws(Exception::class)
    abstract override fun call(): V
}