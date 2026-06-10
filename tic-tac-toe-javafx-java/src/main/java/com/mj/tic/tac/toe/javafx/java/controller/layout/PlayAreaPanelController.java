package com.mj.tic.tac.toe.javafx.java.controller.layout;

import com.mj.tic.tac.toe.javafx.java.controller.BaseController;
import com.mj.tic.tac.toe.javafx.java.controller.Subscriber;
import com.mj.tic.tac.toe.javafx.java.dialog.ConfirmDialog;
import com.mj.tic.tac.toe.javafx.java.engine.WinnerDetector;
import com.mj.tic.tac.toe.javafx.java.task.ComputerMoveTask;
import com.mj.tic.tac.toe.javafx.java.task.GameOverTask;
import com.mj.tic.tac.toe.javafx.java.task.MarkCellTask;
import com.mj.tic.tac.toe.javafx.java.util.Board;
import com.mj.tic.tac.toe.javafx.java.util.Coordinates;
import com.mj.tic.tac.toe.javafx.java.util.Difficulty;
import com.mj.tic.tac.toe.javafx.java.util.GameState;
import com.mj.tic.tac.toe.javafx.java.util.Mark;
import com.mj.tic.tac.toe.javafx.java.util.Player;
import com.mj.tic.tac.toe.javafx.java.util.Winner;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

/**
 * Controls the central Tic-Tac-Toe board panel where all gameplay interactions
 * take place.
 *
 * <p>This controller manages the entire lifecycle of a Tic-Tac-Toe match on the
 * 3&times;3 board, including:</p>
 * <ul>
 *   <li><strong>Cell-click handling</strong> &mdash; when the user clicks a
 *       board cell, a {@link MarkCellTask} is submitted to the background
 *       executor to write the move onto the {@link Board} model.</li>
 *   <li><strong>Computer AI move execution</strong> &mdash; when the turn
 *       changes to the computer, a {@link ComputerMoveTask} is dispatched to
 *       compute the AI's next move. The resulting cell is fired with a
 *       synthetic mouse event to trigger the normal marking pipeline.</li>
 *   <li><strong>Winner detection</strong> &mdash; after each move,
 *       {@link WinnerDetector#detect(Board, Coordinates)} is called to check
 *       whether the current player has completed a winning line. If a winner
 *       is found (or the board is full), a {@link GameOverTask} is dispatched
 *       through the mediator.</li>
 *   <li><strong>State subscription</strong> &mdash; implements
 *       {@link Subscriber} to react to published {@link GameState} transitions:
 *       <ul>
 *         <li>{@link GameState#RESET_GAME} &rarr; clears difficulty, resets
 *             board visuals.</li>
 *         <li>{@link GameState#NEW_GAME} &rarr; sets the chosen difficulty
 *             and randomly selects the starting player.</li>
 *         <li>{@link GameState#GAME_OVER} &rarr; shows a
 *             {@link com.mj.tic.tac.toe.javafx.java.dialog.ConfirmDialog
 *             ConfirmDialog} with a play-again option.</li>
 *       </ul></li>
 * </ul>
 *
 * <h2>Threading model</h2>
 * <p>This controller owns a dedicated single-thread
 * {@link java.util.concurrent.ExecutorService} configured with daemon threads.
 * All background work (marking cells, computing AI moves, game-over
 * presentation) runs on this executor, keeping the JavaFX Application Thread
 * free for UI updates. The executor shuts down automatically when the JVM
 * exits because its threads are daemon threads.</p>
 *
 * <h2>Inner class architecture</h2>
 * <p>Three {@link javafx.beans.value.ChangeListener} inner classes handle
 * reactive wiring:</p>
 * <ul>
 *   <li>{@link OnPlayAreaDisableStateChanged} &mdash; resets board visuals
 *       (clears button text, removes winner highlighting) when the board
 *       becomes enabled.</li>
 *   <li>{@link OnDifficultyValueChanged} &mdash; randomly chooses the
 *       starting player and resets the board model when a new difficulty is
 *       selected.</li>
 *   <li>{@link OnPlayerValueChanged} &mdash; triggers a computer move when
 *       the active player changes to {@link Player#COMPUTER}.</li>
 * </ul>
 *
 * <p><b>FXML wiring:</b> The associated FXML view defines a 3&times;3
 * {@link javafx.scene.layout.GridPane GridPane} of {@code JFXButton}
 * elements, each wired to {@link #onCellClicked(MouseEvent)}.</p>
 *
 * @author Montaser Jamal
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @created 27-05-2026
 * @see Board The game board model
 * @see WinnerDetector Detects winning conditions
 * @see ComputerMoveTask AI move computation
 * @see MarkCellTask Human move recording
 * @see GameOverTask End-of-game presentation
 * @since 1.0.0
 */

public final class PlayAreaPanelController extends BaseController implements Subscriber<Object> {

    /**
     * Single-thread daemon executor for all background gameplay tasks.
     *
     * <p>Daemon threads ensure the executor does not prevent JVM shutdown when
     * the application window is closed, eliminating the need for explicit
     * shutdown hooks. All move computation, board mutation, and game-over
     * presentation tasks run on this executor.</p>
     */
    private final ExecutorService executor = Executors.newSingleThreadExecutor(run -> new Thread(run) {{
        setDaemon(true);
    }});

    /**
     * The current AI difficulty level property.
     *
     * <p>When {@code null}, the board is disabled and no moves can be made.
     * Binding the board's {@code disableProperty} to
     * {@code difficulty.isNull()} provides this behavior automatically.</p>
     */
    private final ObjectProperty<Difficulty> difficulty = new SimpleObjectProperty<>();

    /**
     * The player whose turn it is currently.
     *
     * <p>Alternates between {@link Player#HUMAN} and {@link Player#COMPUTER}
     * after each move. When set to {@code COMPUTER}, an
     * {@link OnPlayerValueChanged} listener automatically triggers
     * AI move computation.</p>
     */
    private final ObjectProperty<Player> player = new SimpleObjectProperty<>();

    /**
     * The backing data model for the Tic-Tac-Toe board.
     *
     * <p>A single {@link Board} instance lives for the duration of the game
     * session. It is {@link Board#reset() reset} either when a new difficulty
     * is selected or when a {@link GameState#RESET_GAME} transition occurs.</p>
     */
    private final Board board = new Board();
    @FXML
    public GridPane rootPane;
    /**
     * Reference to the application's root {@code StackPane}, obtained during
     * {@link #initialize(URL, ResourceBundle)}.
     *
     * <p>Used as the parent container for modal dialogs such as the
     * {@link com.mj.tic.tac.toe.javafx.java.dialog.ConfirmDialog
     * ConfirmDialog} shown when a game ends.</p>
     */
    private StackPane applicationPane;

    /**
     * Initializes the board controller after its FXML view has been loaded.
     *
     * <p>Performs the following setup:</p>
     * <ol>
     *   <li>Obtains a reference to the application root pane for dialog
     *       placement.</li>
     *   <li>Registers a listener that resets button visuals when the board
     *       becomes enabled.</li>
     *   <li>Binds the board's disabled state to the absence of a chosen
     *       difficulty (no difficulty = board locked).</li>
     *   <li>Registers a listener on the difficulty property to pick a
     *       random starting player whenever a new difficulty is set.</li>
     *   <li>Registers a listener on the player property to trigger a
     *       computer move whenever the turn switches to the AI.</li>
     * </ol>
     *
     * @param url       the FXML location, or {@code null} if unknown
     * @param resources the resource bundle, or {@code null} if none provided
     */
    @Override
    public void initialize(URL url, ResourceBundle resources) {
        Platform.runLater(() -> applicationPane = (StackPane) rootPane.getScene().getRoot());
        rootPane.disableProperty().addListener(new OnPlayAreaDisableStateChanged());
        rootPane.disableProperty().bind(difficulty.isNull());
        difficulty.addListener(new OnDifficultyValueChanged());
        player.addListener(new OnPlayerValueChanged());
    }

    /**
     * Handles a click on a board cell button.
     *
     * <p>Creates a {@link MarkCellTask} for the clicked cell and the current
     * player, attaches the common success handler ({@link #onTaskSucceeded}),
     * and submits it to the background executor. The button is automatically
     * disabled by {@code MarkCellTask.scheduled()} to prevent double-clicks
     * while the move is being processed.</p>
     *
     * @param event the mouse-click event whose source is the clicked
     *              {@link javafx.scene.control.Button Button}
     */
    @FXML
    private void onCellClicked(MouseEvent event) {
        Button button = (Button) event.getSource();

        Task<?> task = new MarkCellTask(board, button, player.get());
        task.setOnSucceeded(this::onTaskSucceeded);
        executor.execute(task);
    }

    /**
     * Reacts to game-state changes published by the {@link
     * com.mj.tic.tac.toe.javafx.java.controller.GameStateManager
     * GameStateManager}.
     *
     * <p>Dispatches based on the {@link GameState}:
     * <ul>
     *   <li>{@link GameState#RESET_GAME} &mdash; sets difficulty to
     *       {@code null} (which disables the board via binding) and
     *       resets all cell visuals.</li>
     *   <li>{@link GameState#NEW_GAME} &mdash; extracts the
     *       {@link Difficulty} payload, briefly clears difficulty to
     *       trigger a board reset, then sets the new difficulty
     *       (which re-enables the board and starts a game).</li>
     *   <li>{@link GameState#GAME_OVER} &mdash; shows a
     *       {@link com.mj.tic.tac.toe.javafx.java.dialog.ConfirmDialog
     *       ConfirmDialog} with a localized message (win/loss/draw)
     *       and a "play again" option that triggers a new game with
     *       the same difficulty.</li>
     * </ul></p>
     *
     * @param state the new {@link GameState} that was published
     * @param value the associated payload; expected types are
     *              {@link Difficulty} for {@code NEW_GAME} and
     *              {@link Player} (winner) for {@code GAME_OVER};
     *              may be {@code null}
     */
    @Override
    public void update(GameState state, Object value) {
        switch (state) {
            case RESET_GAME:
                difficulty.set(null);
                new OnPlayAreaDisableStateChanged().changed(null, null, false);
                break;
            case NEW_GAME:
                Stream.of(value)
                        .peek(v -> difficulty.set(null))
                        .map(Difficulty.class::cast)
                        .findFirst()
                        .ifPresent(difficulty::set);
                break;
            case GAME_OVER:
                var messageKey = "message.alert.player.draw";
                if (Objects.nonNull(value)) {
                    messageKey = Optional.of(value)
                            .map(Player.class::cast)
                            .filter(Player.HUMAN::equals)
                            .map(player -> "message.alert.player.winner.human")
                            .orElse("message.alert.player.winner.computer");
                }

                var lMessage = super.getLocalizedText(messageKey);
                var difficulty = this.difficulty.get();
                this.difficulty.set(null);

                Runnable onPlayAgain = () -> PlayAreaPanelController.this.update(GameState.NEW_GAME, difficulty);
                ConfirmDialog.show(applicationPane, lMessage, onPlayAgain);
                break;
        }
    }

    /**
     * Handles successful completion of a background gameplay task.
     *
     * <p>Dispatches based on the concrete task type:</p>
     * <ul>
     *   <li>{@link ComputerMoveTask} &mdash; extracts the chosen
     *       {@link Coordinates} from the task result, computes the
     *       corresponding child index in the board grid, and fires
     *       a synthetic {@link MouseEvent#MOUSE_CLICKED} event on
     *       that cell's button. This simulates a human click and
     *       triggers the normal marking pipeline.</li>
     *   <li>{@link MarkCellTask} &mdash; extracts the
     *       {@link Coordinates} from the returned {@link Mark},
     *       runs {@link WinnerDetector#detect(Board, Coordinates)}
     *       to check for a winner. If a winner is found or the board
     *       is full, delegates a {@link GameOverTask} to the
     *       mediator. Otherwise, switches the active player.</li>
     * </ul>
     *
     * @param event the worker-state event whose source is the completed task
     */
    private void onTaskSucceeded(WorkerStateEvent event) {
        Worker<?> worker = event.getSource();
        if (worker instanceof ComputerMoveTask) {
            var task = (ComputerMoveTask) worker;
            Optional<Coordinates> oCoordinates = task.getValue();
            if (oCoordinates.isPresent()) {
                var coordinates = oCoordinates.get();
                var index = coordinates.getX() * board.getDimension() + coordinates.getY();
                var mEvent = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY,
                        1, false, false, false, false, false, false, false, false, false, false, null);
                rootPane.getChildren().get(index).fireEvent(mEvent);
            }
        } else if (worker instanceof MarkCellTask) {
            var coordinates = ((Mark) worker.getValue()).getCoordinates();
            Optional<Winner> oWinner = WinnerDetector.detect(board, coordinates);
            if (oWinner.isPresent() || board.isFull()) {
                super.mediator.execute(new GameOverTask(rootPane, oWinner.orElse(null)));
            } else {
                Optional.of(player)
                        .map(ObjectProperty::get)
                        .map(Player::opponent)
                        .ifPresent(player::set);
            }
        }
    }

    //================================================={Inner classes}==================================================

    /**
     * Listener that resets all board-cell visuals when the board transitions
     * from disabled to enabled.
     *
     * <p>When the board becomes enabled (e.g. after a difficulty is selected
     * or after a reset), this listener iterates over all child buttons and:
     * <ul>
     *   <li>Clears the button text (removes the previous X/O marks).</li>
     *   <li>Re-enables each button.</li>
     *   <li>Removes the {@code :winner} CSS pseudo-class from any
     *       previously highlighted winning cells.</li>
     * </ul></p>
     */
    private class OnPlayAreaDisableStateChanged implements ChangeListener<Boolean> {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (!newValue) {
                for (Node node : rootPane.getChildren()) {
                    Button button = (Button) node;
                    button.setText(null);
                    button.setDisable(false);
                    button.pseudoClassStateChanged(PseudoClass.getPseudoClass("winner"), false);
                }
            }
        }
    }

    /**
     * Listener that reacts to changes in the AI difficulty level.
     *
     * <p>When a new difficulty is set:
     * <ul>
     *   <li>Randomly selects the starting player (50/50 coin flip between
     *       {@link Player#HUMAN} and {@link Player#COMPUTER}).</li>
     *   <li>Resets the board model via {@link Board#reset()}.</li>
     * </ul>
     * When the difficulty is cleared (set to {@code null}), the player
     * property is also cleared.</p>
     */
    private class OnDifficultyValueChanged implements ChangeListener<Difficulty> {

        @Override
        public void changed(ObservableValue<? extends Difficulty> observable, Difficulty oldValue, Difficulty newValue) {
            if (Objects.nonNull(newValue)) {
                Optional.of(Math.random())
                        .map(Math::round)
                        .map(value -> value + 1)
                        .map(Long::byteValue)
                        .map(Player::from)
                        .ifPresent(player::set);
            } else {
                player.set(null);
            }

            board.reset();
        }
    }

    /**
     * Listener that triggers a computer move when the active player changes
     * to {@link Player#COMPUTER}.
     *
     * <p>Creates a {@link ComputerMoveTask} with the current board state,
     * difficulty, and computer player mark, attaches the common success
     * handler, and submits it to the background executor. The AI
     * computation runs entirely on a background thread, keeping the UI
     * responsive.</p>
     */
    private class OnPlayerValueChanged implements ChangeListener<Player> {

        @Override
        public void changed(ObservableValue<? extends Player> observable, Player oldValue, Player newValue) {
            if (newValue == Player.COMPUTER) {
                var task = new ComputerMoveTask(board, difficulty.get(), newValue);
                task.setOnSucceeded(PlayAreaPanelController.this::onTaskSucceeded);
                executor.execute(task);
            }
        }
    }
}
