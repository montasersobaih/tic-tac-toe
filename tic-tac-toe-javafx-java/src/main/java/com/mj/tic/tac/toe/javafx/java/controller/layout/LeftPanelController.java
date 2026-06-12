package com.mj.tic.tac.toe.javafx.java.controller.layout;

import com.mj.tic.tac.toe.javafx.java.controller.BaseController;
import com.mj.tic.tac.toe.javafx.java.controller.ControllerMediator;
import com.mj.tic.tac.toe.javafx.java.controller.Subscriber;
import com.mj.tic.tac.toe.javafx.java.task.PlayGameTask;
import com.mj.tic.tac.toe.javafx.java.task.ResetGameTask;
import com.mj.tic.tac.toe.javafx.java.util.Difficulty;
import com.mj.tic.tac.toe.javafx.java.util.GameState;
import com.mj.tic.tac.toe.javafx.java.util.Player;
import com.mj.tic.tac.toe.javafx.java.util.Winner;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * Controls the left-side panel of the Tic-Tac-Toe application window,
 * responsible for displaying game session statistics and providing action
 * buttons to start a new game or reset the session.
 * <p>
 * This controller listens for {@link GameState} transitions via the
 * {@link Subscriber} interface and updates its UI elements accordingly:
 * <ul>
 *   <li><strong>{@link GameState#NEW_GAME}</strong> &mdash; clears the win
 *       history, resets all counters to zero, and updates the displayed
 *       difficulty level to the chosen {@link Difficulty} (or the default
 *       label if none was selected).</li>
 *   <li><strong>{@link GameState#RESET_GAME}</strong> &mdash; no action
 *       taken.</li>
 *   <li><strong>{@link GameState#GAME_OVER}</strong> &mdash; examines the
 *       published winner {@link Player} value and increments the appropriate
 *       counter (human wins, machine wins, or draw).</li>
 * </ul>
 * <p>
 * <b>FXML wiring:</b> The associated view defines two action buttons which
 * invoke {@link #onPlayGame(MouseEvent)} and {@link #onResetGame(MouseEvent)}
 * respectively, as well as several {@code Label} and {@code ListView} controls
 * bound in this controller's {@code @FXML} fields.
 * <p>
 * <b>Communication pattern:</b> This controller does not communicate directly
 * with other controllers. Instead, it uses the inherited
 * {@link ControllerMediator} to
 * delegate task execution ({@link com.mj.tic.tac.toe.javafx.java.task.PlayGameTask},
 * {@link com.mj.tic.tac.toe.javafx.java.task.ResetGameTask}) and receives state
 * updates through the publish-subscribe {@link com.mj.tic.tac.toe.javafx.java.controller.GameStateManager}.
 *
 * @author Montaser Jamal
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @created 27-05-2026
 * @since 1.0.0
 */

public final class LeftPanelController extends BaseController implements Subscriber<Object> {

    /**
     * The root {@code BorderPane} of the left panel, injected by the FXMLLoader.
     * Exposed as public to allow parent controllers to apply CSS classes,
     * manage visibility, or access the node tree if needed.
     */
    @FXML
    public BorderPane rootPane;
    /**
     * Reference to the root {@code StackPane} of the entire application scene,
     * obtained during {@link #initialize(URL, ResourceBundle)}. Used as the
     * parent container when displaying modal dialogs such as the difficulty
     * chooser.
     */
    private StackPane applicationPane;
    /**
     * Label that displays the currently selected AI difficulty level
     * (e.g. "Easy", "Medium", "Hard"). Updated on each
     * {@link GameState#NEW_GAME} transition with the chosen
     * {@link Difficulty} &mdash; falls back to a localized default label
     * when no difficulty has been set.
     */
    @FXML
    private Label displayDifficulty;

    /**
     * Scrollable list view that records the outcome of each completed match
     * during the current session. Each entry is a textual description of the
     * game result. This list is cleared when the user starts a new game or
     * resets the session.
     */
    @FXML
    private ListView<String> wins;

    /**
     * Label displaying the total number of draws (ties) across all matches
     * played in the current session. Incremented automatically when a game
     * ends without a winner ({@link GameState#GAME_OVER} with no associated
     * {@link Player}).
     */
    @FXML
    private Label totalDraw;

    /**
     * Label displaying the total number of matches won by the human player
     * during the current session. Incremented when a game ends and the
     * {@link Winner} indicates the {@link Player#HUMAN} as the victor.
     */
    @FXML
    private Label totalHumanWins;

    /**
     * Label displaying the total number of matches won by the computer (AI)
     * during the current session. Incremented when a game ends and the
     * {@link Winner} indicates the {@link Player#COMPUTER} as the victor.
     */
    @FXML
    private Label totalMachineWins;

    /**
     * Initializes the controller after the FXML view has been loaded.
     * <p>
     * Retrieves a reference to the application's root {@code StackPane} from
     * the scene graph for later use as the parent container of modal dialogs.
     *
     * @param url       the location used to resolve relative paths for the
     *                  root object, or {@code null} if the location is unknown.
     * @param resources the resources used to localize the root object, or
     *                  {@code null} if no resource bundle was provided.
     */
    @Override
    public void initialize(URL url, ResourceBundle resources) {
        Platform.runLater(() -> applicationPane = (StackPane) rootPane.getScene().getRoot());
        wins.getItems().addListener(new OnListChangeListener());
    }

    /**
     * Handles the <em>Reset Game</em> button click event.
     * <p>
     * Performs a full session reset:
     * <ol>
     *   <li>Clears the match-history list.</li>
     *   <li>Resets the draw, human-win, and machine-win counters to zero.</li>
     *   <li>Restores the difficulty label to its default localized text.</li>
     *   <li>Delegates a {@link ResetGameTask} to the
     *       {@link ControllerMediator}
     *       to notify the board and other components of the reset.</li>
     * </ol>
     *
     * @param event the mouse event that triggered the action; may be
     *              {@code null} when invoked programmatically (e.g. from
     *              {@link #update(GameState, Object)} on a new game).
     */
    @FXML
    private void onResetGame(MouseEvent event) {
        wins.getItems().clear();
        List.of(totalDraw, totalHumanWins, totalMachineWins)
                .forEach(label -> label.setText("0"));

        if (Objects.nonNull(event)) {
            Optional.of("control.label.difficulty.default")
                    .map(super::getLocalizedText)
                    .ifPresent(displayDifficulty::setText);
            super.mediator.execute(new ResetGameTask());
        }
    }

    /**
     * Handles the <em>Play Game</em> button click event.
     * <p>
     * Creates a {@link PlayGameTask} that opens a difficulty-selection dialog
     * and delegates it to the
     * {@link ControllerMediator}
     * for execution. The dialog uses the application pane as its visual
     * container.
     *
     * @param event the mouse event that triggered the action.
     */
    @FXML
    private void onPlayGame(MouseEvent event) {
        var task = new PlayGameTask(applicationPane);
        super.mediator.execute(task);
    }

    /**
     * Reacts to game-state changes published by the
     * {@link com.mj.tic.tac.toe.javafx.java.controller.GameStateManager}.
     * <p>
     * <strong>State handling:</strong>
     * <ul>
     *   <li>{@link GameState#RESET_GAME} &mdash; no action taken.</li>
     *   <li>{@link GameState#NEW_GAME} &mdash; delegates to
     *       {@link #onResetGame(MouseEvent)} to clear all counters and the
     *       win history, then updates the difficulty label to reflect the
     *       selected {@link Difficulty} (cast from {@code value}). If
     *       {@code value} is {@code null}, the label is left unchanged.</li>
     *   <li>{@link GameState#GAME_OVER} &mdash; resolves the winning
     *       {@link Player} from {@code value} (which may be {@code null} for
     *       a draw) and:
     *       <ul>
     *         <li>Appends a localized result string (win or draw) to the
     *             match-history {@link #wins} list.</li>
     *         <li>Increments the appropriate counter:
     *             {@link Player#HUMAN} &rarr; {@link #totalHumanWins},
     *             {@link Player#COMPUTER} &rarr; {@link #totalMachineWins},
     *             or a draw &rarr; {@link #totalDraw}.</li>
     *       </ul></li>
     * </ul>
     *
     * @param state the new {@link GameState} that was published
     * @param value an optional payload associated with the state transition;
     *              expected to be a {@link Difficulty} for
     *              {@code NEW_GAME} or a {@link Player} (the winner) for
     *              {@code GAME_OVER}; {@code null} for a draw or
     *              {@code RESET_GAME}
     */
    @Override
    public void update(GameState state, Object value) {
        switch (state) {
            case RESET_GAME:
                break;
            case NEW_GAME:
                Optional.of(value)
                        .map(Difficulty.class::cast)
                        .map(Difficulty::toString)
                        .map(super::getLocalizedText)
                        .ifPresent(localizedText -> {
                            LeftPanelController.this.onResetGame(null);
                            displayDifficulty.setText(localizedText);
                        });
                break;
            case GAME_OVER:
                var wPlayer = (Player) value;

                var messageKey = "message.alert.player.draw";
                var totalWinsLabel = totalDraw;
                if (Objects.nonNull(wPlayer)) {
                    messageKey = Optional.of(wPlayer)
                            .filter(Player.HUMAN::equals)
                            .map(player -> "message.alert.player.winner.human")
                            .orElse("message.alert.player.winner.computer");
                    totalWinsLabel = Optional.of(wPlayer)
                            .filter(Player.HUMAN::equals)
                            .map(player -> totalHumanWins)
                            .orElse(totalMachineWins);
                }

                Optional.of(messageKey)
                        .map(super::getLocalizedText)
                        .map(string -> string.split(", ")[0])
                        .ifPresent(wins.getItems()::add);
                Optional.of(totalWinsLabel)
                        .map(Label::getText)
                        .map(Integer::parseInt)
                        .map(wins -> wins + 1)
                        .map(String::valueOf)
                        .ifPresent(totalWinsLabel::setText);
                break;
        }
    }

    //================================================={Inner classes}==================================================

    private class OnListChangeListener implements javafx.collections.ListChangeListener<String> {

        @Override
        public void onChanged(Change<? extends String> c) {
            if (c.next() && c.wasAdded()) {
                Platform.runLater(() -> wins.scrollTo(c.getList().size() - 1));
            }
        }
    }
}
