package com.mj.tic.tac.toe.javafx.java.controller;

import javafx.concurrent.Task;

/**
 * Defines the communication contract used by child controllers to delegate
 * background task execution to a coordinating controller.
 *
 * <p>This mediator keeps controllers loosely coupled by preventing child
 * controllers from directly depending on the parent controller's implementation
 * details. A child controller can submit a JavaFX {@link Task} through this
 * interface, while the mediator decides how the task should be configured,
 * scheduled, and observed.</p>
 *
 * <p>Implementations should keep execution details, such as the underlying
 * executor service or task event handling policy, hidden from callers. This
 * allows the application to change its threading strategy without changing each
 * controller that needs to run background work.</p>
 *
 * @author Montaser Jamal
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @created 29-05-2026
 * @since 1.0.0
 */

public interface ControllerMediator {

    /**
     * Schedules the given JavaFX task for execution.
     *
     * <p>Controllers should call this method when they need to run work in the
     * background and allow the mediator to handle task execution consistently.
     * The mediator implementation may attach common event handlers, submit the
     * task to an executor, or apply any other application-level task management
     * behavior.</p>
     *
     * @param task the JavaFX task to execute.
     */
    void execute(Task<?> task);
}
