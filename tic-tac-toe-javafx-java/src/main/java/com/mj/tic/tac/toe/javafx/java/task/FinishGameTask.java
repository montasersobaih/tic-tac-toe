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

    private final Winner winner;

    private final Pane playAreaPane;

    private final ListView<String> wins;

    private final Label increaseWinnerLabel;

    public FinishGameTask(Winner winner, Pane playAreaPane, ListView<String> wins, Label increaseWinnerLabel) {
        this.winner = winner;
        this.playAreaPane = playAreaPane;
        this.wins = wins;
        this.increaseWinnerLabel = increaseWinnerLabel;
    }

    @Override
    protected void scheduled() {
        this.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, this::onTaskThrowEvent);
    }

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

    private void onTaskThrowEvent(WorkerStateEvent event) {
        if (event.getEventType() == WorkerStateEvent.WORKER_STATE_SUCCEEDED) {
            wins.getItems().add(getMessage().split(", ")[0]);

            int oldValue = Integer.parseInt(increaseWinnerLabel.getText());
            increaseWinnerLabel.setText(String.valueOf(oldValue + 1));
        }
    }
}
