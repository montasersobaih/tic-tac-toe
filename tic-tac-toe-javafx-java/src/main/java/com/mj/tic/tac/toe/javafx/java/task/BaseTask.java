package com.mj.tic.tac.toe.javafx.java.task;

import javafx.concurrent.Task;

abstract class BaseTask<V> extends Task<V> {

    @Override
    protected abstract V call() throws Exception;
}
