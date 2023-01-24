package com.mj.tic.tac.toe.javafx.java.controller.view;

import com.mj.tic.tac.toe.javafx.java.controller.BaseController;
import com.mj.tic.tac.toe.javafx.java.controller.layout.ApplicationBarController;
import com.mj.tic.tac.toe.javafx.java.dialog.ConfirmDialog;
import com.mj.tic.tac.toe.javafx.java.task.CheckWinnerTask;
import com.mj.tic.tac.toe.javafx.java.task.FinishGameTask;
import com.mj.tic.tac.toe.javafx.java.task.MarkCellTask;
import com.mj.tic.tac.toe.javafx.java.task.ResetPlayAreaTask;
import com.mj.tic.tac.toe.javafx.java.util.Mark;
import com.mj.tic.tac.toe.javafx.java.util.Winner;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

public final class ViewController extends BaseController {

    @FXML
    private StackPane viewPane;

    @FXML
    private ApplicationBarController applicationBarController;

    @FXML
    private StackPane contentPane;

    @FXML
    private ListView<String> wins;

    @FXML
    private Label totalDraw;

    @FXML
    private Label totalXWins;

    @FXML
    private Label totalOWins;

    @FXML
    private GridPane playAreaPane;

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        viewPane.sceneProperty().addListener((i1, i2, scene) -> {
            scene.windowProperty().addListener((i3, i4, window) -> {
                window.setOnCloseRequest(ignored -> executor.shutdown());
            });
        });

        applicationBarController.setTitle(getString("control.label.title.app"));
    }

    private void resetPlayArea() {
        Task<?> task = new ResetPlayAreaTask(matrix, playAreaPane);
        task.setOnSucceeded(this::onTaskSucceeded);
        executor.execute(task);
    }

    @FXML
    private void onResetGame(MouseEvent event) {
        wins.getItems().clear();
        totalDraw.setText("0");
        totalXWins.setText("0");
        totalOWins.setText("0");
        playAreaPane.setDisable(true);
        this.resetPlayArea();
    }

    @FXML
    private void onPlayGame(MouseEvent event) {
        playAreaPane.setDisable(false);
        this.resetPlayArea();
    }

    @FXML
    private void onCellClicked(MouseEvent event) {
        Button button = (Button) event.getSource();

        Task<?> task = new MarkCellTask(matrix, button, super.getTurn());
        task.setOnSucceeded(this::onTaskSucceeded);
        executor.execute(task);
    }

    private void onTaskSucceeded(WorkerStateEvent event) {
        Worker<?> worker = event.getSource();
        if (worker instanceof ResetPlayAreaTask) {
            super.resetCount();
        } else if (worker instanceof MarkCellTask) {
            Mark mark = (Mark) worker.getValue();

            Task<?> task = new CheckWinnerTask(matrix, mark);
            task.setOnSucceeded(this::onTaskSucceeded);
            executor.execute(task);
        } else if (worker instanceof CheckWinnerTask) {
            Winner winner = (Winner) worker.getValue();
            if (Objects.nonNull(winner) || super.nextCount() == 9) {
                Label label;
                if (Objects.nonNull(winner)) {
                    label = super.getTurn() == 0 ? totalXWins : totalOWins;
                } else {
                    label = totalDraw;
                }

                Task<?> task = new FinishGameTask(winner, playAreaPane, wins, label);
                task.setOnSucceeded(this::onTaskSucceeded);
                executor.execute(task);
            }
        } else if (worker instanceof FinishGameTask) {
            ConfirmDialog.getInstance(contentPane)
                    .setMessage(worker.getMessage())
                    .setOnConfirmListener(this::resetPlayArea)
                    .setOnDeclineListener(() -> playAreaPane.setDisable(true))
                    .build()
                    .show();
        }
    }
}
