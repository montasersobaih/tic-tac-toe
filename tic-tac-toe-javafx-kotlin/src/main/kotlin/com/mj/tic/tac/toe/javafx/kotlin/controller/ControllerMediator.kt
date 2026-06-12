package com.mj.tic.tac.toe.javafx.kotlin.controller

import javafx.concurrent.Task

/**
 * Functional interface that decouples controller-to-controller
 * communication by abstracting background task execution.
 *
 * Part of the Mediator pattern: child controllers (e.g.,
 * [com.mj.tic.tac.toe.javafx.kotlin.controller.layout.LeftPanelController])
 * do not execute tasks directly but instead delegate to the mediator,
 * which is implemented by [ViewController]. This centralizes task
 * scheduling, result handling, and error management.
 *
 * Defined as a Kotlin [fun interface] (SAM), allowing both
 * class-based and lambda-based implementations.
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

fun interface ControllerMediator {

    /**
     * Executes the given background task.
     *
     * The implementation is responsible for scheduling the task on
     * an appropriate thread (typically a background executor) and
     * attaching result-handling callbacks.
     *
     * @param task The [Task] to execute. The task's result type is
     *   unchecked; the mediator implementation knows which concrete
     *   task types to expect and how to route their results.
     */
    fun execute(task: Task<*>)
}
