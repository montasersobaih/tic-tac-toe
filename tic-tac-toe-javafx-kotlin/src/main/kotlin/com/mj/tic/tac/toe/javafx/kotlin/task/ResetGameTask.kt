package com.mj.tic.tac.toe.javafx.kotlin.task

/**
 * A marker task that signals a game reset.
 *
 * This task performs no actual background work — its sole purpose is
 * to be dispatched through the [ControllerMediator] so that
 * [ViewController.onTaskSucceeded] can publish a [GameState.RESET_GAME]
 * event to all subscribers. The actual reset logic (clearing the board,
 * resetting UI) is handled by the subscribers ([PlayAreaPanelController],
 * [LeftPanelController]) in response to the published event.
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

class ResetGameTask : BaseTask<Void?>() {

    /**
     * Returns null. The side effect is the event publication that
     * follows task completion.
     *
     * @return Always null.
     */
    override fun call(): Void? {
        return null
    }
}
