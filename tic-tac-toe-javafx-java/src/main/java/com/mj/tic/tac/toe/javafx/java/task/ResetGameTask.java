package com.mj.tic.tac.toe.javafx.java.task;

/**
 * A marker {@link javafx.concurrent.Task Task} that signals a game-session reset
 * through the application's task-mediator pipeline.
 *
 * <p>This task performs no actual background work — its {@link #call()} method
 * returns {@code null} immediately. Its sole purpose is to be submitted via
 * {@link com.mj.tic.tac.toe.javafx.java.controller.ControllerMediator#execute(javafx.concurrent.Task)
 * ControllerMediator.execute()} so that the
 * {@link com.mj.tic.tac.toe.javafx.java.controller.view.ViewController ViewController}
 * can recognize its type in the success handler and publish
 * {@link com.mj.tic.tac.toe.javafx.java.util.GameState#RESET_GAME RESET_GAME}
 * to all subscribed controllers.</p>
 *
 * <p>This pattern keeps the reset-triggering logic decoupled: the caller
 * (e.g. a button handler in {@code LeftPanelController}) creates the task and
 * hands it to the mediator without caring how the reset is propagated. The
 * {@code ViewController} translates the task completion into a
 * {@link com.mj.tic.tac.toe.javafx.java.util.GameState GameState} publication,
 * and each subscriber reacts to the state independently.</p>
 *
 * @author Montaser Jamal
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @created 22-05-2026
 * @see BaseTask
 * @see com.mj.tic.tac.toe.javafx.java.controller.ControllerMediator ControllerMediator
 * @see com.mj.tic.tac.toe.javafx.java.util.GameState GameState
 * @since 1.0.0
 */

public final class ResetGameTask extends BaseTask<Void> {

    /**
     * Constructs a new {@code ResetGameTask}.
     *
     * <p>No arguments are required because the task carries no state;
     * its only function is to serve as a type-identifiable signal.</p>
     */
    public ResetGameTask() {}

    /**
     * Performs no work and returns immediately.
     *
     * <p>The return value is always {@code null}. The real effect of
     * this task is the {@link com.mj.tic.tac.toe.javafx.java.util.GameState#RESET_GAME
     * RESET_GAME} publication that the
     * {@link com.mj.tic.tac.toe.javafx.java.controller.view.ViewController ViewController}
     * triggers upon completion.</p>
     *
     * @return always {@code null}
     */
    @Override
    protected Void call() throws Exception {
        return null;
    }
}
