package com.mj.tic.tac.toe.javafx.java.task;

import com.mj.tic.tac.toe.javafx.java.dialog.DifficultyDialog;
import com.mj.tic.tac.toe.javafx.java.util.Difficulty;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;

/**
 * A {@link javafx.concurrent.Task Task} that prompts the user to select an AI
 * difficulty level by showing a modal {@link DifficultyDialog} and returns the
 * chosen {@link Difficulty}.
 *
 * <p>This task is designed to be executed on a background thread (e.g. via a
 * {@link javafx.concurrent.Service Service} or
 * {@link java.util.concurrent.ExecutorService ExecutorService}) while still
 * ensuring the JavaFX dialog is shown on the JavaFX Application Thread. If
 * {@code call()} is already running on the FX thread, the dialog is displayed
 * synchronously; otherwise, the dialog is dispatched via
 * {@link javafx.application.Platform#runLater Platform.runLater()} and the
 * calling thread blocks on a {@link java.util.concurrent.FutureTask FutureTask}
 * until the user makes a selection.</p>
 *
 * <p>The dialog is created and managed entirely by
 * {@link DifficultyDialog#showAndWait(StackPane)} &mdash; this task simply
 * bridges the blocking call into the correct thread context.</p>
 *
 * <p><b>Usage example (with an {@code ExecutorService}):</b></p>
 * <pre>{@code
 * ExecutorService executor = Executors.newSingleThreadExecutor();
 * PlayGameTask task = new PlayGameTask(container);
 * Future<Difficulty> future = executor.submit(task);
 *
 * // ... later ...
 * Difficulty chosen = future.get();
 * }</pre>
 *
 * <p><b>Usage example (with a {@code Service}):</b></p>
 * <pre>{@code
 * Service<Difficulty> service = new Service<>() {
 *     @Override
 *     protected Task<Difficulty> createTask() {
 *         return new PlayGameTask(container);
 *     }
 * };
 * service.setOnSucceeded(event -> {
 *     Difficulty d = service.getValue();
 *     // start game with difficulty d
 * });
 * service.start();
 * }</pre>
 *
 * @author Montaser Jamal
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @created 22-05-2026
 * @see BaseTask
 * @see DifficultyDialog
 * @see Difficulty
 * @since 1.0.0
 */

public final class PlayGameTask extends BaseTask<Difficulty> {

    /**
     * The application pane that hosts the difficulty selection dialog overlay.
     */
    private final StackPane container;

    /**
     * Constructs a new {@code PlayGameTask} bound to the given container.
     *
     * @param container the application {@link StackPane} that will host the
     *                  difficulty dialog overlay; must not be {@code null}
     */
    public PlayGameTask(StackPane container) {
        this.container = container;
    }

    /**
     * Shows the difficulty selection dialog and returns the player's choice.
     *
     * <p>If the current thread is the JavaFX Application Thread, the dialog is
     * shown and awaited synchronously. Otherwise, the dialog is dispatched to
     * the JavaFX Application Thread via {@link Platform#runLater(Runnable)} and
     * the calling thread blocks on a {@link FutureTask} until the user makes a
     * selection or closes the dialog.</p>
     *
     * @return the selected {@link Difficulty}, or {@code null} if the dialog was
     * closed without a selection
     * @throws ExecutionException   if the dialog threw an exception
     * @throws InterruptedException if the current thread is interrupted while
     *                              waiting for the dialog result
     */
    @Override
    protected Difficulty call() throws ExecutionException, InterruptedException {
        Callable<Difficulty> callable = () -> DifficultyDialog.showAndWait(container);
        RunnableFuture<Difficulty> blockingTask = new FutureTask<>(callable);

        if (Platform.isFxApplicationThread()) {
            blockingTask.run();
        } else {
            Platform.runLater(blockingTask);
        }

        return blockingTask.get();
    }
}
