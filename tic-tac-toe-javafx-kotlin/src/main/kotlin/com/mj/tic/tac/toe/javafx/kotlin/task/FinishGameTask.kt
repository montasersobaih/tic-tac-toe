package com.mj.tic.tac.toe.javafx.kotlin.task

import com.mj.tic.tac.toe.javafx.kotlin.util.ResourceBundleUtil
import com.mj.tic.tac.toe.javafx.kotlin.util.Winner
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
import javafx.css.PseudoClass
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.layout.Pane
import java.util.Objects

/**
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 25-01-2023
 */

class FinishGameTask : Task<Void?> {

    private val winner: Winner

    private val playAreaPane: Pane

    private val wins: ListView<String>

    private val winnerLabel: Label

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(winner: Winner, playAreaPane: Pane, wins: ListView<String>, winnerLabel: Label) : super() {
        this.winner = winner
        this.playAreaPane = playAreaPane
        this.wins = wins
        this.winnerLabel = winnerLabel
    }

    override fun scheduled() {
        this.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED) { onTaskThrowEvent(it) }
    }

    override fun call(): Void? {
        if (Objects.nonNull(winner)) {
            val buttons: List<Node> = playAreaPane.children
            for ((x, y) in winner.coordinates) {
                val button = buttons[x * 3 + y] as Button
                Platform.runLater { button.pseudoClassStateChanged(PseudoClass.getPseudoClass("winner"), true) }
            }

            val message = ResourceBundleUtil.getString("message.alert.player.winner")
            updateMessage(String.format(message, winner.player))
        } else {
            updateMessage(ResourceBundleUtil.getString("message.alert.player.draw"))
        }
        return null
    }

    private fun onTaskThrowEvent(event: WorkerStateEvent) {
        if (event.eventType == WorkerStateEvent.WORKER_STATE_SUCCEEDED) {
            wins.items.add(message.split(", ".toRegex())[0])
            winnerLabel.text = (winnerLabel.text.toInt() + 1).toString()
        }
    }
}