package com.mj.tic.tac.toe.javafx.java.task;

import com.mj.tic.tac.toe.javafx.java.util.Coordinates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CheckWinnerTask extends BaseTask<List<Coordinates>> {

    private final byte[][] matrix;

    private final Coordinates location;

    public CheckWinnerTask(byte[][] matrix, Coordinates location) {
        this.matrix = matrix;
        this.location = location;
    }

    @Override
    protected List<Coordinates> call() throws Exception {
        byte value = matrix[location.getX()][location.getY()];
        List<Coordinates> coordinates = new ArrayList<>();

        //check row
        for (int i = 0; i < matrix[location.getX()].length; i++) {
            if (matrix[location.getX()][i] == value) {
                coordinates.add(new Coordinates(location.getX(), i));
                if (coordinates.size() == 3) {
                    return coordinates;
                }
            }
        }

        //check column
        coordinates.clear();
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i][location.getY()] == value) {
                coordinates.add(new Coordinates(location.getX(), i));
                if (coordinates.size() == 3) {
                    return coordinates;
                }
            }
        }

        //check diagonal
        coordinates.clear();
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i][i] == value) {
                coordinates.add(new Coordinates(location.getX(), i));
                if (coordinates.size() == 3) {
                    return coordinates;
                }
            }
        }

        //check reverse diagonal
        coordinates.clear();
        for (int i = matrix.length - 1; i >= 0; i--) {
            int lastIndex = matrix.length - 1;
            if (matrix[lastIndex - i][i] == value) {
                coordinates.add(new Coordinates(location.getX(), i));
                if (coordinates.size() == 3) {
                    return coordinates;
                }
            }
        }

        return Collections.emptyList();
    }
}
