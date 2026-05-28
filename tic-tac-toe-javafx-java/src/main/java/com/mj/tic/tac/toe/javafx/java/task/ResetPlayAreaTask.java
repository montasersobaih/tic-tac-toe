package com.mj.tic.tac.toe.javafx.java.task;

import java.util.List;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

/**
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 22-01-2023
 */

public final class ResetPlayAreaTask extends BaseTask<Void> {

    /**
     * The 3×3 game board to clear (all cells set back to 0).
     */
    private final byte[][] board;

    /**
     * The pane containing the board's button grid to reset.
     */
    private final Pane playAreaPane;

    /**
     * Constructs a new reset-play-area task.
     *
     * @param board        The game board to clear.
     * @param playAreaPane The pane containing the board buttons to reset.
     */
    public ResetPlayAreaTask(byte[][] board, Pane playAreaPane) {
        this.board = board;
        this.playAreaPane = playAreaPane;
    }

    /**
     * Clears each board cell on the background task thread and schedules the
     * matching play-area button to be restored on the JavaFX Application Thread.
     * The button lookup assumes a square board whose buttons are stored in
     * row-major order in {@code playAreaPane.getChildren()}.
     *
     * @return Always {@code null}; this task updates the board and UI by side effect.
     */
    @Override
    protected Void call() {
        List<Node> buttons = playAreaPane.getChildren();

        var bLength = board.length;
        for (int i = 0; i < bLength; i++) {
            for (int j = 0; j < bLength; j++) {
                board[i][j] = (byte) 0;
                Button button = (Button) buttons.get(i * bLength + j);
                Platform.runLater(() -> this.resetButton(button));
            }
        }

        return null;
    }

    /**
     * Resets a single button to its initial state: clears its text, enables it, and removes
     * the "winner" CSS pseudo-class. Must be called on the JavaFX Application Thread.
     *
     * @param button The button to reset.
     */
    private void resetButton(Button button) {
        button.setText(null);
        button.setDisable(false);
        button.pseudoClassStateChanged(PseudoClass.getPseudoClass("winner"), false);
    }
}
