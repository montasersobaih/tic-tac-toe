package com.mj.tic.tac.toe.javafx.kotlin.task

import com.mj.tic.tac.toe.javafx.kotlin.dialog.DifficultyDialog
import com.mj.tic.tac.toe.javafx.kotlin.util.Difficulty
import java.util.concurrent.Callable
import java.util.concurrent.FutureTask
import javafx.application.Platform
import javafx.scene.layout.StackPane

/**
 * Background task that shows the difficulty selection dialog.
 *
 * This task bridges the gap between the background executor thread
 * and the JavaFX Application Thread: it shows the [DifficultyDialog]
 * (which must run on the FX thread) and returns the user's choice.
 *
 * The task uses a [FutureTask] wrapping [DifficultyDialog.showAndWait]:
 * - If already on the FX thread, the dialog runs directly.
 * - Otherwise, it's dispatched via [Platform.runLater].
 * - The background (calling) thread blocks on [FutureTask.get] until
 *   the dialog is closed and the result is available.
 *
 * @property container The [StackPane] serving as the dialog overlay
 *   container, passed through to [DifficultyDialog.showAndWait].
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

class PlayGameTask(private val container: StackPane) : BaseTask<Difficulty?>() {

    /**
     * Shows the difficulty dialog and returns the user's selection.
     *
     * @return The chosen [Difficulty], or null if the dialog was
     *   dismissed without selecting a difficulty.
     * @throws Exception if the dialog interaction fails.
     */
    override fun call(): Difficulty? {
        val callable = Callable<Difficulty?> { DifficultyDialog.showAndWait(container) }
        val blockingTask = FutureTask(callable)

        if (Platform.isFxApplicationThread()) {
            blockingTask.run()
        } else {
            Platform.runLater(blockingTask)
        }

        return blockingTask.get()
    }
}
