package com.mj.tic.tac.toe.javafx.java.task;

import com.mj.tic.tac.toe.javafx.java.util.Coordinates;
import com.mj.tic.tac.toe.javafx.java.util.Player;
import com.mj.tic.tac.toe.javafx.java.util.Winner;
import java.util.List;
import java.util.Objects;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

/**
 * A {@link javafx.concurrent.Task Task} that visually marks the winning cells
 * on the board and then disables the board, signaling the end of the game.
 *
 * <p>When a game of Tic-Tac-Toe concludes (a player wins or the board is
 * full), this task is responsible for the end-of-game presentation:
 * <ol>
 *   <li>In {@link #call()} &mdash; iterates over the winning
 *       {@link Coordinates} from the supplied {@link Winner} object, looks up
 *       the corresponding {@link javafx.scene.control.Button Button} children
 *       in the board's {@link GridPane}, and applies the CSS pseudo-class
 *       {@code :winner} to each via
 *       {@link javafx.application.Platform#runLater Platform.runLater()}.
 *       If there is no winner (i.e. a draw), no highlighting occurs.</li>
 *   <li>In {@link #succeeded()} &mdash; disables the entire board
 *       ({@code boardPane.setDisable(true)}) so no further moves can be made.
 *       This runs automatically on the JavaFX Application Thread after
 *       {@code call()} completes successfully.</li>
 * </ol>
 *
 * <p>CSS authors can style winning cells with a selector such as
 * {@code .button:winner} to apply a highlight color, animation, or other
 * visual treatment.</p>
 *
 * <p>Use this task with a {@link javafx.concurrent.Service Service} or
 * submit it to an {@link java.util.concurrent.ExecutorService ExecutorService}.
 * The result type is {@link Player} &mdash; the winning player, or
 * {@code null} if the game ended in a draw.</p>
 *
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @see Winner
 * @see Coordinates
 * @see PseudoClass
 * @see BaseTask
 * @since 25-01-2023
 */

public final class GameOverTask extends BaseTask<Player> {

    /**
     * The board grid whose cell buttons will be highlighted for the win.
     */
    private final GridPane boardPane;

    /**
     * The game outcome, including the winning player and the cells that
     * form the winning line. May be {@code null} to indicate a draw.
     */
    private final Winner winner;

    /**
     * Constructs a new {@code GameOverTask}.
     *
     * @param boardPane the board {@link GridPane} whose winning cells will
     *                  be highlighted; must not be {@code null}
     * @param winner    the game outcome ({@link Winner}) containing the
     *                  winning player and the winning cell coordinates, or
     *                  {@code null} if the game ended in a draw
     */
    public GameOverTask(GridPane boardPane, Winner winner) {
        this.boardPane = boardPane;
        this.winner = winner;
    }

    /**
     * Highlights the winning cells on the board with the {@code :winner} CSS
     * pseudo-class.
     *
     * <p>If a {@link Winner} is present, this method iterates over its
     * winning {@link Coordinates}, computes each cell's index in the
     * {@link GridPane} children list using the formula
     * {@code index = x * rowCount + y}, retrieves the corresponding
     * {@link Button}, and applies the {@code :winner} pseudo-class via
     * {@link Platform#runLater(Runnable)}. If {@code winner} is {@code null}
     * (a draw), no highlighting is performed.</p>
     *
     * @return the winning {@link Player}, or {@code null} if the game was
     * a draw
     */
    @Override
    protected Player call() {
        Player player = null;

        if (Objects.nonNull(winner)) {
            player = winner.getPlayer();

            var boardDimension = boardPane.getRowCount();
            List<Node> buttons = boardPane.getChildren();
            for (Coordinates location : winner.getLocations()) {
                var index = location.getX() * boardDimension + location.getY();
                Button button = (Button) buttons.get(index);
                Platform.runLater(() -> button.pseudoClassStateChanged(PseudoClass.getPseudoClass("winner"), true));
            }
        }

        return player;
    }
}
