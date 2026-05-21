package com.mj.tic.tac.toe.javafx.java.task;

import javafx.concurrent.Task;

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

abstract class BaseTask<V> extends Task<V> {

    /**
     * Invoked on a background thread to perform the task's work.
     *
     * @return The computed result, or {@code null} if the task produces no value.
     */
    @Override
    protected abstract V call() throws Exception;
}
