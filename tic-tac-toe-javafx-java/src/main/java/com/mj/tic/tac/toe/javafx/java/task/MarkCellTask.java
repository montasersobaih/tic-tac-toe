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
     * The two playable symbols — index 0 = 'X' (first player), index 1 = 'O' (second player).
     */
    private final char[] symbols = {'X', 'O'};

    /**
     * The 3×3 byte board backing the game (0 = empty, 1 = player 1, 2 = player 2).
     */
    private final byte[][] board;

    /**
     * The JavaFX {@link Button} that was clicked, representing the cell to mark.
     */
    private final Button button;

    /**
     * The player number that has made this move.
     */
    private final byte player;

    /**
     * Constructs a new mark-cell task for the player's move.
     *
     * @param board  The 3×3 game board to update.
     * @param button The button corresponding to the cell the player clicked.
     * @param player The player number that made this move.
     */
    public MarkCellTask(byte[][] board, Button button, byte player) {
        this.board = board;
        this.button = button;
        this.player = player;
    }

    /**
     * Executed on a background thread. Disables the clicked button to prevent re-use, extracts
     * the button's grid row and column via {@link GridPane#getRowIndex} and
     * {@link GridPane#getColumnIndex}, writes the player's marker into the board, and
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

        Platform.runLater(this::setPlayerSymbol);
        board[i][j] = (byte) (player + 1);

        return new Mark(symbols[player], coordinates);
    }

    /**
     * Updates the button's text to the current player's symbol ('X' or 'O').
     * This method must be called on the JavaFX Application Thread, typically via
     * {@link Platform#runLater}.
     */
    private void setPlayerSymbol() {
        button.setText(Character.toString(symbols[player]));
    }
}
