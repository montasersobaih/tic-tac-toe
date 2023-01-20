package com.mj.tic.tac.toe.javafx.java.controller.view;

import com.mj.tic.tac.toe.javafx.java.controller.BaseController;
import com.mj.tic.tac.toe.javafx.java.controller.layout.ApplicationBarController;
import com.mj.tic.tac.toe.javafx.java.dialog.ConfirmDialog;
import com.mj.tic.tac.toe.javafx.java.task.CheckWinnerTask;
import com.mj.tic.tac.toe.javafx.java.util.Coordinates;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ViewController extends BaseController {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final byte[][] matrix = new byte[3][3];

    private final char[] players = {'X', 'O'};

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
        playAreaPane.getChildren()
                .stream()
                .filter(node -> node instanceof Button)
                .map(Button.class::cast)
                .forEach(button -> button.setOnMouseClicked(this::onCellClicked));
    }

    private void play() {
        for (byte[] line : matrix) {
            Arrays.fill(line, (byte) 0);
        }

        count = 0;
        for (Node node : playAreaPane.getChildren()) {
            Button button = (Button) node;
            button.setText(null);
            button.setDisable(false);
        }
    }

    private void onCellClicked(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setDisable(true);

        int i = GridPane.getRowIndex(button);
        int j = GridPane.getColumnIndex(button);
        Coordinates coordinates = new Coordinates(i, j);

        button.setText(Character.toString(players[count % 2]));
        matrix[i][j] = (byte) ((count++ % 2) + 1);

        CheckWinnerTask checkWinnerTask = new CheckWinnerTask(matrix, coordinates);
        checkWinnerTask.setOnSucceeded(this::onTaskSucceeded);
        executor.execute(checkWinnerTask);
    }

    @FXML
    private void onResetGame(MouseEvent event) {
        wins.getItems().clear();
        totalDraw.setText("0");
        totalXWins.setText("0");
        totalOWins.setText("0");
        playAreaPane.setDisable(true);
        playAreaPane.getChildren()
                .stream()
                .filter(node -> node instanceof Button)
                .map(Button.class::cast)
                .forEach(button -> {
                    button.setText(null);
                    button.setDisable(false);
                });
    }

    @FXML
    private void onPlayGame(MouseEvent event) {
        playAreaPane.setDisable(false);
        this.play();
    }

    private void onTaskSucceeded(WorkerStateEvent event) {
        Worker<?> worker = event.getSource();
        if (worker instanceof CheckWinnerTask) {
            List<Coordinates> coordinates = ((CheckWinnerTask) worker).getValue();
            if (coordinates.size() == 3 || count == 9) {
                String message;
                if (coordinates.size() == 3) {
                    int pIndex = (count - 1) % 2;
                    if (pIndex == 0) {
                        int oldValue = Integer.parseInt(totalXWins.getText());
                        totalXWins.setText(String.valueOf(oldValue + 1));
                    } else {
                        int oldValue = Integer.parseInt(totalOWins.getText());
                        totalOWins.setText(String.valueOf(oldValue + 1));
                    }

                    char player = players[pIndex];
                    message = getString("message.alert.player.winner");
                    message = String.format(message, player);
                } else {
                    int oldValue = Integer.parseInt(totalDraw.getText());
                    totalDraw.setText(String.valueOf(oldValue + 1));

                    message = getString("message.alert.player.draw");
                }

                ConfirmDialog.getInstance(contentPane)
                        .setMessage(message)
                        .setOnConfirmListener(this::play)
                        .setOnDeclineListener(() -> playAreaPane.setDisable(true))
                        .build()
                        .show();
            }
        }
    }
}
