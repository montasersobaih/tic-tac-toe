package com.mj.tic.tac.toe.javafx.java.task;

import com.mj.tic.tac.toe.javafx.java.util.Coordinates;
import com.mj.tic.tac.toe.javafx.java.util.Mark;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

/**
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 22-01-2023
 */

public final class MarkCellTask extends BaseTask<Mark> {

    private final char[] player = {'X', 'O'};

    private final byte[][] matrix;

    private final Button button;

    private final int turn;

    public MarkCellTask(byte[][] matrix, Button button, int turn) {
        this.matrix = matrix;
        this.button = button;
        this.turn = turn;
    }

    @Override
    protected Mark call() {
        button.setDisable(true);

        int i = GridPane.getRowIndex(button);
        int j = GridPane.getColumnIndex(button);
        Coordinates coordinates = new Coordinates(i, j);

        Platform.runLater(this::setPlayer);
        matrix[i][j] = (byte) (turn + 1);

        return new Mark(player[turn], coordinates);
    }

    private void setPlayer() {
        button.setText(Character.toString(player[turn]));
    }
}
