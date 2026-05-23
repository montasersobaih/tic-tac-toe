package com.mj.tic.tac.toe.javafx.java.task;

import com.mj.tic.tac.toe.javafx.java.util.Coordinates;
import com.mj.tic.tac.toe.javafx.java.util.Mark;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

/**
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 22-01-2023
 */

public final class MarkCellTask extends BaseTask<Mark> {

    /**
     * The two player symbols — index 0 = 'X' (first player), index 1 = 'O' (second player).
     */
    private final char[] player = {'X', 'O'};

    /**
     * The 3×3 byte matrix backing the game board (0 = empty, 1 = player 1, 2 = player 2).
     */
    private final byte[][] matrix;

    /**
     * The JavaFX {@link Button} that was clicked, representing the cell to mark.
     */
    private final Button button;

    /**
     * The current turn index (0 for the first player, 1 for the second).
     */
    private final int turn;

    /**
     * Constructs a new mark-cell task for the player's move.
     *
     * @param matrix The 3×3 game board matrix to update.
     * @param button The button corresponding to the cell the player clicked.
     * @param turn   The current turn index (0 = 'X', 1 = 'O').
     */
    public MarkCellTask(byte[][] matrix, Button button, int turn) {
        this.matrix = matrix;
        this.button = button;
        this.turn = turn;
    }

    /**
     * Executed on a background thread. Disables the clicked button to prevent re-use, extracts
     * the button's grid row and column via {@link GridPane#getRowIndex} and
     * {@link GridPane#getColumnIndex}, writes the player's marker into the board matrix, and
     * schedules the button's text to be set on the JavaFX Application Thread.
     *
     * @return A {@link Mark} containing the player's symbol ('X' or 'O') and the cell's
     * {@link Coordinates}.
     */
    @Override
    protected Mark call() {
        button.setDisable(true);

        int i = GridPane.getRowIndex(button);
        int j = GridPane.getColumnIndex(button);
        Coordinates coordinates = new Coordinates(i, j);

        Platform.runLater(this::setPlayer);
        matrix[i][j] = (byte) (turn + 1);

        return new Mark(player[turn], coordinates);
    }

    /**
     * Updates the button's text to the current player's symbol ('X' or 'O').
     * This method must be called on the JavaFX Application Thread, typically via
     * {@link Platform#runLater}.
     */
    private void setPlayer() {
        button.setText(Character.toString(player[turn]));
    }
}
