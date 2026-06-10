package com.mj.tic.tac.toe.javafx.java.task;

import com.mj.tic.tac.toe.javafx.java.util.Board;
import com.mj.tic.tac.toe.javafx.java.util.Coordinates;
import com.mj.tic.tac.toe.javafx.java.util.Mark;
import com.mj.tic.tac.toe.javafx.java.util.Player;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

/**
 * A background {@link javafx.concurrent.Task} that writes a player's mark into a Tic-Tac-Toe cell.
 *
 * <p>When executed, this task extracts the clicked button's row and column coordinates
 * from its parent {@link GridPane}, records the player number in the shared board array,
 * and returns a {@link Mark} value object carrying the rendered character ({@code 'X'} or
 * {@code 'O'}) together with the cell coordinates.</p>
 *
 * <p><b>Threading contract</b>
 * <ul>
 *   <li>{@link #scheduled()} — runs on the JavaFX Application Thread;
 *       disables the button immediately to prevent double-clicks.</li>
 *   <li>{@link #call()} — runs on a background worker thread;
 *       performs the board mutation and returns the {@link Mark}.</li>
 *   <li>{@link #succeeded()} — runs on the JavaFX Application Thread;
 *       updates the button's text with the player's symbol.</li>
 * </ul>
 * </p>
 *
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 22-01-2023
 */
public final class MarkCellTask extends BaseTask<Mark> {

    /**
     * The two playable symbols — index {@code 0} = {@code 'X'} (first player),
     * index {@code 1} = {@code 'O'} (second player).
     */
    private final char[] symbols = {'X', 'O'};

    /**
     * The 3×3 byte board backing the game state.
     *
     * <ul>
     *   <li>{@code 0} — empty cell</li>
     *   <li>{@code 1} — cell marked by player 1</li>
     *   <li>{@code 2} — cell marked by player 2</li>
     * </ul>
     */
    private final Board board;

    /**
     * The JavaFX {@link Button} that was clicked, representing the cell to mark.
     */
    private final Button button;

    /**
     * The player number that made this move ({@code 1} or {@code 2}).
     */
    private final Player player;

    /**
     * Constructs a new mark-cell task for the player's move.
     *
     * @param board  the 3×3 game board to mutate; must not be {@code null}
     * @param button the button corresponding to the clicked cell; must not be {@code null}
     * @param player the player number making this move (expected values are {@code 1} or {@code 2})
     */
    public MarkCellTask(Board board, Button button, Player player) {
        this.board = board;
        this.button = button;
        this.player = player;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Disables the clicked button to prevent the user from re-marking the same cell
     * while the task is running.</p>
     */
    @Override
    protected void scheduled() {
        button.setDisable(true);
    }

    /**
     * Performs the core marking operation on a background thread.
     *
     * <p>The method retrieves the clicked button's row and column indices from its
     * {@link GridPane} parent, persists the player number into the corresponding
     * board cell, and returns a {@link Mark} describing the move.</p>
     *
     * @return a {@link Mark} containing the player's symbol ({@code 'X'} or {@code 'O'})
     * and the cell's {@link Coordinates}
     */
    @Override
    protected Mark call() {
        int x = GridPane.getRowIndex(button);
        int y = GridPane.getColumnIndex(button);
        Coordinates coordinates = new Coordinates(x, y);

        board.mark(coordinates, player);
        return new Mark(symbols[board.getMarkedCellsCount() % 2], coordinates);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Invoked on the JavaFX Application Thread after the task completes successfully.
     * Sets the button's text to the player's symbol ({@code 'X'} or {@code 'O'}) so the
     * UI reflects the updated board state.</p>
     */
    @Override
    protected void succeeded() {
        button.setText(Character.toString(getValue().getSymbol()));
    }
}
