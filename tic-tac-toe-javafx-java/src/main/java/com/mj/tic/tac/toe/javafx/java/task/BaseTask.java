package com.mj.tic.tac.toe.javafx.java.task;

import javafx.concurrent.Task;

/**
 * Package-private abstract base class for all application background tasks.
 *
 * <p>This class extends {@link javafx.concurrent.Task Task&lt;V&gt;} and
 * redeclares {@link #call()} as abstract, providing a common supertype for
 * every task executed within the application's mediator-executor pipeline.
 * Concrete subclasses implement specific game operations such as marking a
 * cell, computing an AI move, or handling game-over presentation.</p>
 *
 * <p>Tasks are executed through the
 * {@link com.mj.tic.tac.toe.javafx.java.controller.ControllerMediator
 * ControllerMediator} interface and are submitted to a shared single-thread
 * executor, ensuring sequential, non-overlapping background work.</p>
 *
 * <p><b>Package-private visibility</b> &mdash; only classes within the
 * {@code task} package may extend this base. All application tasks are
 * expected to reside in this package.</p>
 *
 * @param <V> the result type returned by the task's {@link #call()} method
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @see com.mj.tic.tac.toe.javafx.java.task.MarkCellTask
 * @see com.mj.tic.tac.toe.javafx.java.task.ComputerMoveTask
 * @see com.mj.tic.tac.toe.javafx.java.task.GameOverTask
 * @see com.mj.tic.tac.toe.javafx.java.task.PlayGameTask
 * @see com.mj.tic.tac.toe.javafx.java.task.ResetGameTask
 * @since 20-01-2023
 */

abstract class BaseTask<V> extends Task<V> {

    /**
     * Invoked on a background thread to perform the task's work.
     *
     * <p>Subclasses must implement this method to define the actual work
     * performed off the JavaFX Application Thread. The return value is
     * delivered to any {@link javafx.concurrent.Task#setOnSucceeded
     * onSucceeded} handler registered on this task.</p>
     *
     * @return the computed result, or {@code null} if the task produces
     * no value
     * @throws Exception if the background computation fails
     */
    @Override
    protected abstract V call() throws Exception;
}
