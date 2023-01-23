package com.mj.tic.tac.toe.javafx.java.controller.view;

import com.mj.tic.tac.toe.javafx.java.controller.BaseController;
import com.mj.tic.tac.toe.javafx.java.controller.layout.ApplicationBarController;
import com.mj.tic.tac.toe.javafx.java.dialog.ConfirmDialog;
import com.mj.tic.tac.toe.javafx.java.task.CheckWinnerTask;
import com.mj.tic.tac.toe.javafx.java.task.MarkCellTask;
import com.mj.tic.tac.toe.javafx.java.task.ResetPlayAreaTask;
import com.mj.tic.tac.toe.javafx.java.util.Mark;
import com.mj.tic.tac.toe.javafx.java.util.Winner;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

public final class ViewController extends BaseController {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final byte[][] matrix = new byte[3][3];

    private byte count = 0;

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
        ResetPlayAreaTask task = new ResetPlayAreaTask(matrix, playAreaPane);
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

        MarkCellTask task = new MarkCellTask(matrix, button, count % 2);
        task.setOnSucceeded(this::onTaskSucceeded);
        executor.execute(task);
    }

    private void onTaskSucceeded(WorkerStateEvent event) {
        Worker<?> worker = event.getSource();
        if (worker instanceof ResetPlayAreaTask) {
            count = 0;
        } else if (worker instanceof MarkCellTask) {
            Mark mark = (Mark) worker.getValue();
            CheckWinnerTask checkWinnerTask = new CheckWinnerTask(matrix, mark);
            checkWinnerTask.setOnSucceeded(this::onTaskSucceeded);
            executor.execute(checkWinnerTask);
        } else if (worker instanceof CheckWinnerTask) {
            Winner winner = (Winner) worker.getValue();
            if (Objects.nonNull(winner) || ++count == 9) {
                Label label;
                String message;
                if (Objects.nonNull(winner)) {
                    label = count % 2 == 0 ? totalXWins : totalOWins;
                    message = getString("message.alert.player.winner");
                    message = String.format(message, winner.getPlayer());
                } else {
                    label = totalDraw;
                    message = getString("message.alert.player.draw");
                }

                wins.getItems().add(message.split(", ")[0]);

                int oldValue = Integer.parseInt(label.getText());
                label.setText(String.valueOf(oldValue + 1));

                ConfirmDialog.getInstance(contentPane)
                        .setMessage(message)
                        .setOnConfirmListener(this::resetPlayArea)
                        .setOnDeclineListener(() -> playAreaPane.setDisable(true))
                        .build()
                        .show();
            }
        }
    }
}
