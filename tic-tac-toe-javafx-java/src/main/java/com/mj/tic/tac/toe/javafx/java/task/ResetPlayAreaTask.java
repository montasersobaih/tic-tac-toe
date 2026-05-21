package com.mj.tic.tac.toe.javafx.java.task;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.util.Arrays;

/**
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 22-01-2023
 */

public final class ResetPlayAreaTask extends BaseTask<Void> {

    /** The 3×3 game board matrix to clear (all cells set back to 0). */
    private final byte[][] matrix;

    /** The pane containing the board's button grid to reset. */
    private final Pane playAreaPane;

    /**
     * Constructs a new reset-play-area task.
     *
     * @param matrix       The game board matrix to clear.
     * @param playAreaPane The pane containing the board buttons to reset.
     */
    public ResetPlayAreaTask(byte[][] matrix, Pane playAreaPane) {
        this.matrix = matrix;
        this.playAreaPane = playAreaPane;
    }

    /**
     * Executed on a background thread. Fills every cell of the board matrix with 0, then
     * iterates over all child nodes of the play-area pane to reset each button's text,
     * disabled state, and winner pseudo-class on the JavaFX Application Thread.
     *
     * @return Always {@code null} (the result is communicated via UI side effects).
     */
    @Override
    protected Void call() {
        for (byte[] line : matrix) {
            Arrays.fill(line, (byte) 0);
        }

        for (Node node : playAreaPane.getChildren()) {
            Button button = (Button) node;
            Platform.runLater(() -> this.resetButton(button));
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
