package com.mj.tic.tac.toe.javafx.kotlin.task

import com.mj.tic.tac.toe.javafx.kotlin.dialog.DifficultyDialog
import com.mj.tic.tac.toe.javafx.kotlin.util.Difficulty
import java.util.concurrent.Callable
import java.util.concurrent.FutureTask
import javafx.application.Platform
import javafx.scene.layout.StackPane

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 29-05-2026
 */

class PlayGameTask(private val container: StackPane) : BaseTask<Difficulty?>() {

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
