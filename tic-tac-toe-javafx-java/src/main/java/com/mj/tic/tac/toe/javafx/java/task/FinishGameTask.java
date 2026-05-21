package com.mj.tic.tac.toe.javafx.java.task;

import com.mj.tic.tac.toe.javafx.java.util.Coordinates;
import com.mj.tic.tac.toe.javafx.java.util.ResourceBundleUtil;
import com.mj.tic.tac.toe.javafx.java.util.Winner;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.Objects;

/**
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 25-01-2023
 */

public final class FinishGameTask extends Task<Void> {

    /** The winner data (player symbol and winning coordinates), or {@code null} for a draw. */
    private final Winner winner;

    /** The pane containing the board's button grid. */
    private final Pane playAreaPane;

    /** The list view serving as a scoreboard / game history log. */
    private final ListView<String> wins;

    /** The label displaying the total number of games played. */
    private final Label increaseWinnerLabel;

    /**
     * Constructs a new finish-game task.
     *
     * @param winner             The winner data, or {@code null} if the game ended in a draw.
     * @param playAreaPane       The pane containing the board buttons.
     * @param wins               The scoreboard list view to update with the result.
     * @param increaseWinnerLabel The label whose numeric value is incremented each game.
     */
    public FinishGameTask(Winner winner, Pane playAreaPane, ListView<String> wins, Label increaseWinnerLabel) {
        this.winner = winner;
        this.playAreaPane = playAreaPane;
        this.wins = wins;
        this.increaseWinnerLabel = increaseWinnerLabel;
    }

    /**
     * Registers an event handler that fires when the task completes successfully.
     * The handler updates the scoreboard and increments the game counter.
     */
    @Override
    protected void scheduled() {
        this.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, this::onTaskThrowEvent);
    }

    /**
     * Executed on a background thread. If there is a winner, applies a "winner" CSS pseudo-class
     * to the three winning buttons and sets a localized victory message. If the game is a draw,
     * sets a draw message instead.
     *
     * @return Always {@code null} (the result is communicated via UI side effects).
     */
    @Override
    protected Void call() {
        if (Objects.nonNull(winner)) {
            List<Node> buttons = playAreaPane.getChildren();
            for (Coordinates location : winner.getLocations()) {
                Button button = (Button) buttons.get(location.getX() * 3 + location.getY());
                Platform.runLater(() -> button.pseudoClassStateChanged(PseudoClass.getPseudoClass("winner"), true));
            }

            String message = ResourceBundleUtil.getString("message.alert.player.winner");
            updateMessage(String.format(message, winner.getPlayer()));
        } else {
            updateMessage(ResourceBundleUtil.getString("message.alert.player.draw"));
        }

        return null;
    }

    /**
     * Handles the task's success event. Appends the result message (minus the trailing
     * coordinates detail) to the scoreboard list and increments the game-counter label.
     *
     * @param event The worker state event triggered on successful completion.
     */
    private void onTaskThrowEvent(WorkerStateEvent event) {
        if (event.getEventType() == WorkerStateEvent.WORKER_STATE_SUCCEEDED) {
            wins.getItems().add(getMessage().split(", ")[0]);

            int oldValue = Integer.parseInt(increaseWinnerLabel.getText());
            increaseWinnerLabel.setText(String.valueOf(oldValue + 1));
        }
    }
}
