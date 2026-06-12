package com.mj.tic.tac.toe.javafx.java.controller.view;

import com.mj.tic.tac.toe.javafx.java.controller.BaseController;
import com.mj.tic.tac.toe.javafx.java.controller.ControllerMediator;
import com.mj.tic.tac.toe.javafx.java.controller.GameStateManager;
import com.mj.tic.tac.toe.javafx.java.controller.layout.ApplicationBarController;
import com.mj.tic.tac.toe.javafx.java.controller.layout.LeftPanelController;
import com.mj.tic.tac.toe.javafx.java.controller.layout.PlayAreaPanelController;
import com.mj.tic.tac.toe.javafx.java.task.GameOverTask;
import com.mj.tic.tac.toe.javafx.java.task.PlayGameTask;
import com.mj.tic.tac.toe.javafx.java.task.ResetGameTask;
import com.mj.tic.tac.toe.javafx.java.util.Difficulty;
import com.mj.tic.tac.toe.javafx.java.util.GameState;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

/**
 * Root controller of the main application view, responsible for wiring child
 * controllers, mediating background task execution, and publishing game-state
 * transitions.
 *
 * <p>This class is the central orchestrator of the application's
 * Model-View-Controller architecture. It is the controller associated with the
 * top-level FXML layout (e.g. {@code view.fxml}) and contains three named child
 * controllers:
 * <ol>
 *   <li>{@link ApplicationBarController} &mdash; manages the top bar (title,
 *       close button, etc.).</li>
 *   <li>{@link LeftPanelController} &mdash; handles the left sidebar (session
 *       stats, difficulty label, play/reset buttons).</li>
 *   <li>{@link PlayAreaPanelController} &mdash; controls the center board
 *       (grid of cells, game logic, computer AI moves).</li>
 * </ol>
 *
 * <h2>Responsibilities</h2>
 *
 * <h3>1. Mediator pattern ({@link ControllerMediator})</h3>
 * <p>Child controllers never interact with one another directly. Instead, they
 * call {@link #execute(Task)} on the shared mediator (this controller). The
 * {@code ViewController} schedules the task on its single-thread
 * {@link java.util.concurrent.ExecutorService}, attaches a common success
 * handler, and publishes the resulting {@link GameState} to all interested
 * parties via the {@link GameStateManager}.</p>
 *
 * <h3>2. Publish-subscribe ({@link GameStateManager})</h3>
 * <p>During {@link #initialize(URL, ResourceBundle)} the child controllers
 * register themselves as {@link com.mj.tic.tac.toe.javafx.java.controller.Subscriber
 * Subscriber}s for specific {@link GameState}s:
 * <ul>
 *   <li>{@link GameState#RESET_GAME} &rarr; {@link PlayAreaPanelController}</li>
 *   <li>{@link GameState#NEW_GAME} &rarr; {@link LeftPanelController} +
 *       {@link PlayAreaPanelController}</li>
 *   <li>{@link GameState#GAME_OVER} &rarr; {@link LeftPanelController} +
 *       {@link PlayAreaPanelController}</li>
 * </ul>
 * When a task completes and {@link #onTaskSucceeded} publishes a state, all
 * registered subscribers are notified on the JavaFX Application Thread.</p>
 *
 * <h3>3. Task-result dispatching</h3>
 * <p>Each background task produces a result that maps to a game-state
 * transition:
 * <ul>
 *   <li>{@link ResetGameTask} &rarr; {@link GameState#RESET_GAME} (no payload)</li>
 *   <li>{@link PlayGameTask} &rarr; {@link GameState#NEW_GAME} with the chosen
 *       {@link Difficulty}</li>
 *   <li>{@link GameOverTask} &rarr; {@link GameState#GAME_OVER} with the winning
 *       {@link com.mj.tic.tac.toe.javafx.java.util.Player Player} (or
 *       {@code null} for a draw)</li>
 * </ul></p>
 *
 * <h3>4. Lifecycle management</h3>
 * <p>The executor service is cleanly shut down when the application window is
 * closed, preventing resource leaks.</p>
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @see ControllerMediator
 * @see GameStateManager
 * @see GameState
 * @since 20-01-2023
 */

public final class ViewController extends BaseController implements ControllerMediator {

    /**
     * Single-thread executor that runs all background game tasks sequentially.
     * Ensures tasks such as marking cells, computing AI moves, and game-over
     * presentation do not overlap or race.
     */
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Publish-subscribe event bus for game-state transitions.
     * Child controllers subscribe to specific {@link GameState}s during
     * initialization; this controller publishes to them when a background
     * task completes.
     */
    private final GameStateManager gameStateManager = new GameStateManager();

    /**
     * The root {@code StackPane} of the entire view, injected from FXML.
     */
    @FXML
    private StackPane viewPane;

    /**
     * Child controller managing the top application bar (title label, etc.).
     * Injected automatically by the FXMLLoader via the
     * {@code fx:controller} attribute on the included FXML.
     */
    @FXML
    private ApplicationBarController applicationBarController;

    /**
     * Child controller managing the left sidebar (session statistics, difficulty
     * display, play/reset buttons). Injected automatically by the FXMLLoader.
     */
    @FXML
    private LeftPanelController leftPanelController;

    /**
     * Child controller managing the central Tic-Tac-Toe board (grid of cells,
     * cell-click handling, computer AI move coordination). Injected
     * automatically by the FXMLLoader.
     */
    @FXML
    private PlayAreaPanelController playAreaPanelController;

    /**
     * Initializes the root view after its FXML layout has been loaded.
     *
     * <p>Performs the following setup in order:</p>
     * <ol>
     *   <li>Registers a window-close handler that shuts down the background
     *       executor service.</li>
     *   <li>Sets the application title on the
     *       {@link ApplicationBarController}.</li>
     *   <li>Injects this {@code ViewController} as the
     *       {@link ControllerMediator} into the child controllers so they
     *       can delegate background tasks.</li>
     *   <li>Subscribes the child controllers to the appropriate
     *       {@link GameState}s via the {@link GameStateManager}.</li>
     * </ol>
     *
     * @param url       the FXML location or {@code null}
     * @param resources the resource bundle or {@code null}
     */
    @Override
    public void initialize(URL url, ResourceBundle resources) {
        Platform.runLater(() -> viewPane.getScene().getWindow().setOnCloseRequest(event -> executor.shutdown()));

        applicationBarController.setTitle(getLocalizedText("control.label.title.app"));

        Stream.of(leftPanelController, playAreaPanelController)
                .forEach(controller -> controller.setMediator(ViewController.this));

        gameStateManager.subscribe(GameState.RESET_GAME, playAreaPanelController);
        gameStateManager.subscribe(GameState.NEW_GAME, leftPanelController, playAreaPanelController);
        gameStateManager.subscribe(GameState.GAME_OVER, leftPanelController, playAreaPanelController);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Attaches {@link #onTaskSucceeded(WorkerStateEvent)} as the success
     * handler and submits the task to the single-thread executor. Every
     * background task in the application goes through this method, ensuring
     * uniform lifecycle handling.</p>
     */
    @Override
    public void execute(Task<?> task) {
        task.setOnSucceeded(this::onTaskSucceeded);
        executor.execute(task);
    }

    /**
     * Handles successful completion of a background task by publishing the
     * corresponding {@link GameState} to all subscribed controllers.
     *
     * <p>Dispatches based on the concrete task type:</p>
     * <ul>
     *   <li>{@link ResetGameTask} &rarr; publishes
     *       {@link GameState#RESET_GAME} with no payload.</li>
     *   <li>{@link PlayGameTask} &rarr; publishes
     *       {@link GameState#NEW_GAME} with the chosen
     *       {@link Difficulty} as the payload.</li>
     *   <li>{@link GameOverTask} &rarr; publishes
     *       {@link GameState#GAME_OVER} with the winning
     *       {@link com.mj.tic.tac.toe.javafx.java.util.Player Player} (or
     *       {@code null} for a draw).</li>
     * </ul>
     *
     * <p>Publication is itself submitted to the executor so it runs off the
     * JavaFX Application Thread; the {@link GameStateManager} internally
     * dispatches subscriber updates back to the FX thread via
     * {@link javafx.application.Platform#runLater Platform.runLater()}.</p>
     *
     * @param event the worker-state event whose source is the completed task
     */
    public void onTaskSucceeded(WorkerStateEvent event) {
        Worker<?> worker = event.getSource();
        if (worker instanceof ResetGameTask) {
            executor.execute(() -> gameStateManager.publish(GameState.RESET_GAME));
        } else if (worker instanceof PlayGameTask) {
            var difficulty = worker.getValue();
            if (Objects.nonNull(difficulty)) {
                executor.execute(() -> gameStateManager.publish(GameState.NEW_GAME, difficulty));
            }
        } else if (worker instanceof GameOverTask) {
            var wPlayer = worker.getValue();
            executor.execute(() -> gameStateManager.publish(GameState.GAME_OVER, wPlayer));
        }
    }
}
