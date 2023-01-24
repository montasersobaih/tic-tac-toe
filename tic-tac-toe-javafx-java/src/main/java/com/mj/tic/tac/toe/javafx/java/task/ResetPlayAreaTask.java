package com.mj.tic.tac.toe.javafx.java.task;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.util.Arrays;

/**
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 22-01-2023
 */

public final class ResetPlayAreaTask extends BaseTask<Void> {

    private final byte[][] matrix;

    private final Pane playAreaPane;

    public ResetPlayAreaTask(byte[][] matrix, Pane playAreaPane) {
        this.matrix = matrix;
        this.playAreaPane = playAreaPane;
    }

    @Override
    protected Void call() {
        for (byte[] line : matrix) {
            Arrays.fill(line, (byte) 0);
        }

        for (Node node : playAreaPane.getChildren()) {
            Button button = (Button) node;
            Platform.runLater(() -> this.resetButton(button));
        }

        return null;
    }

    private void resetButton(Button button) {
        button.setText(null);
        button.setDisable(false);
        button.pseudoClassStateChanged(PseudoClass.getPseudoClass("winner"), false);
    }
}
